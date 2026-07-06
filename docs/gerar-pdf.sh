#!/bin/bash
# gerar-pdf.sh <documento.md> [saida.pdf]
# Converte um doc Markdown em PDF (marked via npx + Google Chrome headless).
# Ex.: ./gerar-pdf.sh guia-mecanismo.md
set -e
MD="$1"
PDF="${2:-${MD%.md}.pdf}"
TITULO="$(head -1 "$MD" | sed 's/^#* *//')"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

npx --yes marked --gfm -i "$MD" -o "$TMP/body.html"

cat > "$TMP/doc.html" <<HEAD
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="utf-8">
<title>$TITULO</title>
<style>
  @page { size: A4; margin: 18mm 16mm; }
  * { box-sizing: border-box; }
  body { font-family: 'DejaVu Sans', Arial, sans-serif; font-size: 10.5pt; line-height: 1.5;
         color: #1a1a2e; max-width: 100%; margin: 0; }
  h1 { font-size: 19pt; border-bottom: 3px solid #d9531e; padding-bottom: 6px; color: #16213e; }
  h2 { font-size: 14pt; margin-top: 1.6em; border-bottom: 1px solid #ccc; padding-bottom: 3px; color: #16213e; }
  h3 { font-size: 11.5pt; margin-top: 1.3em; color: #16213e; }
  table { border-collapse: collapse; width: 100%; margin: 0.8em 0; font-size: 9.5pt;
          page-break-inside: avoid; }
  th, td { border: 1px solid #bbb; padding: 5px 8px; text-align: left; vertical-align: top; }
  th { background: #16213e; color: #fff; }
  tr:nth-child(even) td { background: #f4f5f7; }
  code { font-family: 'DejaVu Sans Mono', monospace; font-size: 9pt; background: #f0f1f4;
         padding: 1px 4px; border-radius: 3px; }
  pre { background: #f0f1f4; border: 1px solid #ddd; border-radius: 5px; padding: 10px;
        overflow-x: hidden; page-break-inside: avoid; }
  pre code { background: none; padding: 0; white-space: pre-wrap; }
  blockquote { border-left: 4px solid #d9531e; margin: 0.8em 0; padding: 4px 14px;
               background: #fdf3ee; color: #444; }
  hr { border: none; border-top: 1px solid #ccc; margin: 1.6em 0; }
  li { margin: 3px 0; }
</style>
</head>
<body>
HEAD
cat "$TMP/body.html" >> "$TMP/doc.html"
echo "</body></html>" >> "$TMP/doc.html"

google-chrome --headless --disable-gpu --no-pdf-header-footer \
  --print-to-pdf="$PDF" "$TMP/doc.html" 2>/dev/null
echo "PDF gerado: $PDF"
