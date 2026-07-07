-- Numeração humana das vendas ("OS nº 12").
-- Rodar UMA vez no MySQL de cada instalação que já tenha vendas criadas
-- antes deste recurso (MySQL 8.0 Command Line Client → use supermidia; source ...).
-- Instalações novas não precisam: o Hibernate cria a coluna e o sistema numera sozinho.

ALTER TABLE vendas ADD COLUMN numero BIGINT NULL;
ALTER TABLE vendas ADD CONSTRAINT uk_vendas_numero UNIQUE (numero);

-- Backfill: numera as vendas existentes na ordem de criação.
UPDATE vendas v
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY data_criacao) rn FROM vendas) t ON v.id = t.id
SET v.numero = t.rn;
