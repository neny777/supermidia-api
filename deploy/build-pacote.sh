#!/bin/bash
# Monta o pacote de deploy para Windows em ~/workspace/deploy-supermidia/
# (frontend compilado dentro do JAR + start.bat + dump do banco + instruções)
set -e
API="$(cd "$(dirname "$0")/.." && pwd)"
WEB="$API/../supermidia-web"
OUT="$API/../deploy-supermidia"

echo "== 1/5 build do frontend (produção, URL relativa) =="
(cd "$WEB" && npm run build >/dev/null)

echo "== 2/5 embutindo o frontend no backend =="
rm -rf "$API/src/main/resources/static"
cp -r "$WEB/dist" "$API/src/main/resources/static"

echo "== 3/5 empacotando o JAR =="
(cd "$API" && ./mvnw -q clean package -DskipTests)

echo "== 4/5 montando o pacote =="
rm -rf "$OUT" && mkdir -p "$OUT"
cp "$API"/target/supermidia-*.jar "$OUT/supermidia.jar"
JWT=$(openssl rand -base64 32)

# SMTP da recuperação de senha: vem de deploy/mail.env (fora do git).
if [ -f "$API/deploy/mail.env" ]; then
  # shellcheck disable=SC1091
  . "$API/deploy/mail.env"
else
  echo "   AVISO: deploy/mail.env não encontrado — recuperação de senha por e-mail"
  echo "   NÃO funcionará no destino até preencher as variáveis MAIL_* no .bat."
  MAIL_HOST="nao-configurado"; MAIL_PORT="465"
  MAIL_USERNAME="nao-configurado"; MAIL_PASSWORD="nao-configurado"
fi

preencher() { # injeta JWT/SMTP e converte para CRLF (arquivos consumidos no Windows)
  sed -e "s|__JWT_SECRET__|$JWT|" \
      -e "s|__MAIL_HOST__|${MAIL_HOST//&/\\&}|" \
      -e "s|__MAIL_PORT__|${MAIL_PORT//&/\\&}|" \
      -e "s|__MAIL_USERNAME__|${MAIL_USERNAME//&/\\&}|" \
      -e "s|__MAIL_PASSWORD__|${MAIL_PASSWORD//&/\\&}|" \
      "$1" | sed 's/$/\r/' > "$2"
}
preencher "$API/deploy/start-supermidia.bat" "$OUT/start-supermidia.bat"
preencher "$API/deploy/supermidia-service.xml" "$OUT/supermidia-service.xml"
sed 's/$/\r/' "$API/deploy/instalar-servico.bat" > "$OUT/instalar-servico.bat"
sed 's/$/\r/' "$API/deploy/desinstalar-servico.bat" > "$OUT/desinstalar-servico.bat"

# WinSW: roda o jar como serviço do Windows (sem janela; baixa uma vez e guarda)
WINSW="$API/deploy/winsw/WinSW-x64.exe"
if [ ! -f "$WINSW" ]; then
  mkdir -p "$API/deploy/winsw"
  curl -sL -o "$WINSW" https://github.com/winsw/winsw/releases/download/v2.12.0/WinSW-x64.exe
fi
cp "$WINSW" "$OUT/supermidia-service.exe"

[ -f "$API/docs/deploy-windows.pdf" ] && cp "$API/docs/deploy-windows.pdf" "$OUT/LEIA-ME.pdf"
cp "$API/deploy/supermidia.ico" "$OUT/supermidia.ico" # ícone p/ atalho do .bat no Windows

echo "== 5/5 exportando o banco (se o MySQL local estiver no ar) =="
if mysqldump --single-transaction -u root -proot --databases supermidia > "$OUT/supermidia-dump.sql" 2>/dev/null; then
  echo "   dump exportado."
else
  rm -f "$OUT/supermidia-dump.sql"
  echo "   AVISO: MySQL local fora do ar — gere o dump depois com:"
  echo "   mysqldump --single-transaction -u root -p --databases supermidia > supermidia-dump.sql"
fi

echo ""
echo "Pacote pronto em: $OUT"
ls -lh "$OUT"
