-- phpMyAdmin SQL Dump
-- version 5.2.1deb3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Tempo de geração: 26/03/2026 às 00:29
-- Versão do servidor: 8.0.45-0ubuntu0.24.04.1
-- Versão do PHP: 8.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `supermidia`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `calculos`
--

CREATE TABLE `calculos` (
  `id` binary(16) NOT NULL,
  `base_operacional` enum('ALTURA_DUPLA','ALTURA_SIMPLES','AREA','LARGURA_DUPLA','LARGURA_SIMPLES','METRO_LINEAR_INFORMADO','PERIMETRO','QUANTIDADE_INFORMADA') NOT NULL,
  `nome` varchar(140) NOT NULL,
  `permite_override_parametro` bit(1) NOT NULL,
  `permite_override_resultado` bit(1) NOT NULL,
  `tipo_calculo` enum('AREA_BASE','AREA_COM_ACRESCIMOS_E_FATOR','AREA_COM_FATOR','METRO_LINEAR_INFORMADO','PERIMETRO_BASE','PERIMETRO_COM_ESPACAMENTO','QUANTIDADE_INFORMADA','SELECAO_POR_MEDIDA','UNIDADE_FIXA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `calculos`
--

INSERT INTO `calculos` (`id`, `base_operacional`, `nome`, `permite_override_parametro`, `permite_override_resultado`, `tipo_calculo`) VALUES
(0x06d59a533aa240c982b2586ff27b208c, 'AREA', 'MATERIAL COM ACRESCIMOS E FATOR', b'0', b'0', 'AREA_COM_ACRESCIMOS_E_FATOR'),
(0x247c0d3debf042fd9ea714c5bbeec028, 'PERIMETRO', 'EMBAINHAGEM PERÍMETRO', b'0', b'0', 'PERIMETRO_BASE'),
(0x34176c48f0e946ceb4987806275bbc61, 'AREA', 'MATERIAL COM FATOR', b'0', b'0', 'AREA_COM_FATOR'),
(0x4171bad1ce8a4507bca408b86a459408, 'QUANTIDADE_INFORMADA', 'ILHOSAGEM POR QUANTIDADE INFORMADA', b'0', b'0', 'QUANTIDADE_INFORMADA'),
(0x72afd606399a435b9563032b54adaac3, 'PERIMETRO', 'ILHÓS POR ESPAÇAMENTO', b'0', b'0', 'PERIMETRO_COM_ESPACAMENTO'),
(0x74f8deece5134aae81aaddb926244436, 'PERIMETRO', 'REFILE PERÍMETRO', b'0', b'0', 'PERIMETRO_BASE'),
(0xa4b947d56a9b4ef58cbd1569d500976d, 'QUANTIDADE_INFORMADA', 'ILHÓS POR QUANTIDADE INFORMADA', b'0', b'0', 'QUANTIDADE_INFORMADA'),
(0xbc5695f0653a4f4eba574f685a409287, 'AREA', 'IMPRESSÃO ÁREA BASE', b'0', b'0', 'AREA_BASE'),
(0xdd81a7882b3d48a39d5f8cebce8b7b60, 'AREA', 'LONA COM ACRÉSCIMO E FATOR', b'0', b'0', 'AREA_COM_ACRESCIMOS_E_FATOR');

-- --------------------------------------------------------

--
-- Estrutura para tabela `clientes`
--

CREATE TABLE `clientes` (
  `pessoa_id` binary(16) NOT NULL,
  `categoria` enum('F','R') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `clientes`
--

INSERT INTO `clientes` (`pessoa_id`, `categoria`) VALUES
(0x3151279abbd0403096452b19376d7d52, 'R'),
(0x3ae2ac4c447b4eec9e155131b5f87862, 'F'),
(0x4ea52a4a6e8e40eea3efa5e5295a2777, 'R'),
(0xa4df2ca44ab746a1a9317c8bff53575e, 'R'),
(0xa6662d2514e54ac698b94c9ef0176de8, 'F'),
(0xbde31c0f3c7f4b30857dac179c4463e0, 'F'),
(0xcafee7b41c384c769fc7ffb1c5c59b25, 'F');

-- --------------------------------------------------------

--
-- Estrutura para tabela `colaboradores`
--

CREATE TABLE `colaboradores` (
  `pessoa_id` binary(16) NOT NULL,
  `ctps` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `colaboradores`
--

INSERT INTO `colaboradores` (`pessoa_id`, `ctps`) VALUES
(0x182f6f4b8ec74ac3afa744dd1d970068, NULL),
(0x22541355a59b4202959246ed0608391f, NULL),
(0x4ea52a4a6e8e40eea3efa5e5295a2777, NULL),
(0x9447ca75c09a44d7b7bc1a84b3ff51f4, NULL),
(0xa6662d2514e54ac698b94c9ef0176de8, NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `fornecedores`
--

CREATE TABLE `fornecedores` (
  `pessoa_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `fornecedores`
--

INSERT INTO `fornecedores` (`pessoa_id`) VALUES
(0x182f6f4b8ec74ac3afa744dd1d970068),
(0x3151279abbd0403096452b19376d7d52),
(0x3ae2ac4c447b4eec9e155131b5f87862),
(0x504c649a95e54ae9b3a91a8db90d9f93),
(0xa4df2ca44ab746a1a9317c8bff53575e);

-- --------------------------------------------------------

--
-- Estrutura para tabela `materias`
--

CREATE TABLE `materias` (
  `id` binary(16) NOT NULL,
  `nome` varchar(140) NOT NULL,
  `preco` decimal(12,2) NOT NULL,
  `unidade` enum('M','M2','M3','UN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `materias`
--

INSERT INTO `materias` (`id`, `nome`, `preco`, `unidade`) VALUES
(0x03eddf67a6904665bf38d4e58e5ea093, 'ADESIVO VINIL BRILHO GENÉRICO', 8.50, 'M2'),
(0x0a79cf2e594a45518ced40767735a43e, 'BASTÃO 205', 3.50, 'UN'),
(0x0a988478a7d24f0d89ae94910007f610, 'LONA FOSCA GENÉRICA', 8.50, 'M2'),
(0x110739047d0143078a2688070f3dfdd1, 'PONTEIRA PEQUENA', 0.11, 'UN'),
(0x191a6a5a272a4e0b84e227002b3ed8c7, 'BASTÃO 150', 1.50, 'UN'),
(0x5fee831d6bc5442d83bf24b8b78d0763, 'GRAMPO', 0.02, 'UN'),
(0x6dd17ad52ca0497795b0d6eb231a09b3, 'BASTÃO 105', 1.07, 'UN'),
(0x9b2fd8c0a5a34111b5e969edcf044fcf, 'CHAPA PVC 2MM', 45.00, 'M2'),
(0x9bd789be08c1438a811af3ce129e44ee, 'ILHÓS', 0.15, 'UN'),
(0xc42981fd6b1942a6ae94cd62c314140b, 'LONA BRILHO GENÉRICA', 8.50, 'M2'),
(0xc6b750acda0a4afa99675108c080b12b, 'CHAPA PVC 1MM', 17.50, 'M2'),
(0xd90cd3b5282d484791c1d808607802d7, 'INSUMO DE IMPRESSÃO', 8.50, 'M2'),
(0xe352541bff7248f0a1ba18d8e06a7c9d, 'BASTÃO 75', 0.75, 'UN'),
(0xe4e93744f6fd4cd89e82608831e404b1, 'PONTEIRA MÉDIA', 0.24, 'UN'),
(0xfa8d0ee3db164aaa888a3891f2e4feb6, 'CORDÃO', 0.18, 'M');

-- --------------------------------------------------------

--
-- Estrutura para tabela `parceiros`
--

CREATE TABLE `parceiros` (
  `pessoa_id` binary(16) NOT NULL,
  `categoria` enum('F','R') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `parceiros`
--

INSERT INTO `parceiros` (`pessoa_id`, `categoria`) VALUES
(0x22541355a59b4202959246ed0608391f, 'F'),
(0x3151279abbd0403096452b19376d7d52, 'F'),
(0x59d904d089884549baedea04f9130518, 'F');

-- --------------------------------------------------------

--
-- Estrutura para tabela `pessoas`
--

CREATE TABLE `pessoas` (
  `id` binary(16) NOT NULL,
  `bairro` varchar(60) DEFAULT NULL,
  `cep` varchar(9) DEFAULT NULL,
  `email` varchar(70) DEFAULT NULL,
  `logradouro` varchar(60) DEFAULT NULL,
  `municipio` varchar(60) DEFAULT NULL,
  `nome` varchar(60) NOT NULL,
  `numero` varchar(6) DEFAULT NULL,
  `telefone` varchar(15) DEFAULT NULL,
  `uf` varchar(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `pessoas`
--

INSERT INTO `pessoas` (`id`, `bairro`, `cep`, `email`, `logradouro`, `municipio`, `nome`, `numero`, `telefone`, `uf`) VALUES
(0x182f6f4b8ec74ac3afa744dd1d970068, 'JARDIM OURO VERDE III', '37831-260', 'felifeafs@gmail.com', 'RUA HENRIQUE ZACCARO', 'GUAXUPÉ', 'FELIPE AUGUSTO FLORENCIO DOS SANTOS', '423', '(35) 98433-7183', 'MG'),
(0x22541355a59b4202959246ed0608391f, 'JARDIM BRASÍLIA', '13420-005', 'denis.rochajunior2023@gmail.com', 'RUA FRANCISCO DO AMARAL', 'PIRACICABA', 'DENIS ANTÔNIO ROCHA JÚNIOR', '147', '(35) 98825-6010', 'SP'),
(0x3151279abbd0403096452b19376d7d52, 'CRUZ PRETA', '37132-204', 'financeiro@supemidiaalfenas.com.br', 'AVENIDA JOSÉ PAULINO DA COSTA', 'ALFENAS', 'SUPERMÍDIA ALFENAS', '693', '(35) 3291-1516', 'MG'),
(0x3ae2ac4c447b4eec9e155131b5f87862, 'LOTEAMENTO VILA FLORA II', '37700-396', 'poderadesivos@mail.com', 'RUA ANTÔNIO ÉRRICO', 'POÇOS DE CALDAS', 'ADESIVOS PODER LTDA', '500', '(35) 2222-3333', 'MG'),
(0x4ea52a4a6e8e40eea3efa5e5295a2777, 'CRUZ PRETA', '37132-204', NULL, 'AVENIDA JOSÉ PAULINO DA COSTA', 'ALFENAS', 'MARIA JOSÉ DOS SANTOS ROCHA', NULL, '(35) 98876-6249', 'MG'),
(0x504c649a95e54ae9b3a91a8db90d9f93, 'VILA RICA', '37901-060', 'contato@artecoresdistribuicao.com.br', 'RUA MANTIQUEIRA', 'PASSOS', 'ARTE CORES DISTRIBUIÇÃO AGBIS', '950', '(35) 3413-7012', 'MG'),
(0x59d904d089884549baedea04f9130518, 'SAVASSI', '30130-138', 'estacao@mail.com', 'RUA RIO GRANDE DO NORTE', 'BELO HORIZONTE', 'PARCEIRA NOVA ESTAÇÃO', '89', '(35) 55555-5555', 'MG'),
(0x9447ca75c09a44d7b7bc1a84b3ff51f4, 'JARDIM ALVORADA', '37135-250', 'brenda.csr@hotmail.com', 'RUA PLÍNIO LEITE DA SILVA', 'ALFENAS', 'BRENDA CRISTINA SANTOS ROCHA', '1090', '(35) 98832-4447', 'MG'),
(0xa4df2ca44ab746a1a9317c8bff53575e, 'ESPLANADA', '35302-256', 'ti@helenaebenjaminalimentosltda.com.br', 'RUA GERALDO CEVIDANES', 'CARATINGA', 'HELENA E BENJAMIN ALIMENTOS LTDA', '671', '(33) 98209-0041', 'MG'),
(0xa6662d2514e54ac698b94c9ef0176de8, 'CRUZ PRETA', '37132-204', 'denisantoniorocha@gmail.com', 'AVENIDA JOSÉ PAULINO DA COSTA', 'ALFENAS', 'DENIS ANTONIO ROCHA', '693', '(35) 98875-1516', 'MG'),
(0xbde31c0f3c7f4b30857dac179c4463e0, 'CENTRO', '37130-011', 'juarezsilvasiqueira@mail.com', 'RUA PADRE JOÃO BATISTA', 'ALFENAS', 'JUAREZ DA SILVA SIQUEIRA', '223', '(35) 99952-3547', 'MG'),
(0xcafee7b41c384c769fc7ffb1c5c59b25, 'SÃO JOÃO', '32405-610', 'sebastiao.gonzales@mailbrasil.com.br', 'RUA SETE', 'IBIRITÉ', 'SEBASTIÃO GONZALES FAGUNDES', '67', '(35) 9881-2565', 'MG');

-- --------------------------------------------------------

--
-- Estrutura para tabela `pessoas_fisica`
--

CREATE TABLE `pessoas_fisica` (
  `cpf` varchar(14) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `rg` varchar(14) DEFAULT NULL,
  `sexo` enum('F','M') DEFAULT NULL,
  `id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `pessoas_fisica`
--

INSERT INTO `pessoas_fisica` (`cpf`, `data_nascimento`, `rg`, `sexo`, `id`) VALUES
('177.633.616-03', '2008-05-19', NULL, 'M', 0x182f6f4b8ec74ac3afa744dd1d970068),
('121.424.276-69', '2000-02-20', '19807205', 'M', 0x22541355a59b4202959246ed0608391f),
(NULL, NULL, NULL, 'F', 0x4ea52a4a6e8e40eea3efa5e5295a2777),
('164.182.226-01', '2002-12-04', NULL, 'F', 0x9447ca75c09a44d7b7bc1a84b3ff51f4),
('984.816.816-87', '1974-03-30', 'M7.431.361', 'M', 0xa6662d2514e54ac698b94c9ef0176de8),
('802.921.580-09', '1990-05-17', '28.218.082-5', 'M', 0xbde31c0f3c7f4b30857dac179c4463e0),
('795.883.710-07', '1980-11-11', '09812387645', 'M', 0xcafee7b41c384c769fc7ffb1c5c59b25);

-- --------------------------------------------------------

--
-- Estrutura para tabela `pessoas_juridica`
--

CREATE TABLE `pessoas_juridica` (
  `cnpj` varchar(18) DEFAULT NULL,
  `ie` varchar(18) DEFAULT NULL,
  `id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `pessoas_juridica`
--

INSERT INTO `pessoas_juridica` (`cnpj`, `ie`, `id`) VALUES
('06.333.873/0001-00', NULL, 0x3151279abbd0403096452b19376d7d52),
('89.912.240/0001-76', NULL, 0x3ae2ac4c447b4eec9e155131b5f87862),
('13.569.870/0001-28', NULL, 0x504c649a95e54ae9b3a91a8db90d9f93),
('82.273.899/0001-80', '728.695.770/76', 0x59d904d089884549baedea04f9130518),
('31.766.387/0001-15', '397.770.969/24', 0xa4df2ca44ab746a1a9317c8bff53575e);

-- --------------------------------------------------------

--
-- Estrutura para tabela `produtos`
--

CREATE TABLE `produtos` (
  `id` binary(16) NOT NULL,
  `nome` varchar(140) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `produtos`
--

INSERT INTO `produtos` (`id`, `nome`) VALUES
(0x8f42dd9ef1494b698f2c3ab62a5d4bfc, 'LONA BRILHO GENÉRICA'),
(0xc132f52528424e1280ec227836178351, 'LONA BRILHO GENÉRICA COM ILHÓS'),
(0xf4298a4254b0402ba538d1c91eb786ef, 'LONA BRILHO REFILADA');

-- --------------------------------------------------------

--
-- Estrutura para tabela `produtos_materias_calculos`
--

CREATE TABLE `produtos_materias_calculos` (
  `id` binary(16) NOT NULL,
  `calculo_id` binary(16) NOT NULL,
  `materia_id` binary(16) NOT NULL,
  `produto_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `produtos_materias_calculos`
--

INSERT INTO `produtos_materias_calculos` (`id`, `calculo_id`, `materia_id`, `produto_id`) VALUES
(0x2a449d15261b401e985d8017c4b8ae63, 0xbc5695f0653a4f4eba574f685a409287, 0xd90cd3b5282d484791c1d808607802d7, 0xc132f52528424e1280ec227836178351),
(0x527729441125446e88da5036c508ae49, 0xbc5695f0653a4f4eba574f685a409287, 0xd90cd3b5282d484791c1d808607802d7, 0xf4298a4254b0402ba538d1c91eb786ef),
(0x6d25c7bda0154d6e92a84e65aaaf1c30, 0xbc5695f0653a4f4eba574f685a409287, 0xd90cd3b5282d484791c1d808607802d7, 0x8f42dd9ef1494b698f2c3ab62a5d4bfc),
(0xb040af7d89784d74a47f43a8687e52c0, 0x72afd606399a435b9563032b54adaac3, 0x9bd789be08c1438a811af3ce129e44ee, 0xc132f52528424e1280ec227836178351),
(0xc0b38d05384e424c896089aab2a60640, 0x34176c48f0e946ceb4987806275bbc61, 0xc42981fd6b1942a6ae94cd62c314140b, 0xf4298a4254b0402ba538d1c91eb786ef),
(0xe36b4ac74b624ea5b805263e6be1204a, 0x06d59a533aa240c982b2586ff27b208c, 0xc42981fd6b1942a6ae94cd62c314140b, 0xc132f52528424e1280ec227836178351),
(0xf68f586b3a24451f943891fafb4c9d2c, 0x34176c48f0e946ceb4987806275bbc61, 0xc42981fd6b1942a6ae94cd62c314140b, 0x8f42dd9ef1494b698f2c3ab62a5d4bfc);

-- --------------------------------------------------------

--
-- Estrutura para tabela `produtos_materias_parametros_calculos`
--

CREATE TABLE `produtos_materias_parametros_calculos` (
  `id` binary(16) NOT NULL,
  `codigo` enum('ACRESCIMO_ALTURA','ACRESCIMO_LARGURA','ESPACAMENTO','FATOR','QUANTIDADE_FIXA') NOT NULL,
  `valor` decimal(12,4) NOT NULL,
  `produto_materia_calculo_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `produtos_materias_parametros_calculos`
--

INSERT INTO `produtos_materias_parametros_calculos` (`id`, `codigo`, `valor`, `produto_materia_calculo_id`) VALUES
(0x8585129b3e5c4ad49efcc3a70630ce93, 'FATOR', 1.2100, 0xc0b38d05384e424c896089aab2a60640),
(0xd3bd7e1941314d39a40742673a545c84, 'ACRESCIMO_ALTURA', 3.0000, 0xe36b4ac74b624ea5b805263e6be1204a),
(0xded110eab00c43829a11cf5810ffecd6, 'FATOR', 1.2100, 0xe36b4ac74b624ea5b805263e6be1204a),
(0xe748d586434b4a0d91a951de66de6e46, 'ACRESCIMO_LARGURA', 3.0000, 0xe36b4ac74b624ea5b805263e6be1204a),
(0xed8085ad357249aab88eba948920eee2, 'FATOR', 1.2100, 0xf68f586b3a24451f943891fafb4c9d2c);

-- --------------------------------------------------------

--
-- Estrutura para tabela `produtos_servicos_calculos`
--

CREATE TABLE `produtos_servicos_calculos` (
  `id` binary(16) NOT NULL,
  `calculo_id` binary(16) NOT NULL,
  `produto_id` binary(16) NOT NULL,
  `servico_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `produtos_servicos_calculos`
--

INSERT INTO `produtos_servicos_calculos` (`id`, `calculo_id`, `produto_id`, `servico_id`) VALUES
(0x009a4a093747460aa8dffe9d921d82ad, 0xbc5695f0653a4f4eba574f685a409287, 0xc132f52528424e1280ec227836178351, 0x09105b05696347c7a1339bc972ca69b3),
(0x38dc49c806cd43c388ebb01afcd4869d, 0x74f8deece5134aae81aaddb926244436, 0xc132f52528424e1280ec227836178351, 0xd10e647063244ca7b45ed854ef44eb1b),
(0x3a54cce738a545bfb6cdd2e66b34b847, 0xbc5695f0653a4f4eba574f685a409287, 0xf4298a4254b0402ba538d1c91eb786ef, 0x09105b05696347c7a1339bc972ca69b3),
(0x752df0fc35e642dcbb81f97677e1cb7e, 0x74f8deece5134aae81aaddb926244436, 0xf4298a4254b0402ba538d1c91eb786ef, 0xd10e647063244ca7b45ed854ef44eb1b),
(0x824f538e024343c8b245ec571947b7b2, 0x247c0d3debf042fd9ea714c5bbeec028, 0xc132f52528424e1280ec227836178351, 0xa7ef719ce50f411c9fbb90361ddacf7f),
(0xb798fedf0daf4b05ac29fdd4c35b50bd, 0x72afd606399a435b9563032b54adaac3, 0xc132f52528424e1280ec227836178351, 0x353ec9549ad64e48b0bb33f56754ebf5),
(0xf3268f01f4854e4798192762ddb571bc, 0xbc5695f0653a4f4eba574f685a409287, 0x8f42dd9ef1494b698f2c3ab62a5d4bfc, 0x09105b05696347c7a1339bc972ca69b3);

-- --------------------------------------------------------

--
-- Estrutura para tabela `produtos_servicos_parametros_calculos`
--

CREATE TABLE `produtos_servicos_parametros_calculos` (
  `id` binary(16) NOT NULL,
  `codigo` enum('ACRESCIMO_ALTURA','ACRESCIMO_LARGURA','ESPACAMENTO','FATOR','QUANTIDADE_FIXA') NOT NULL,
  `valor` decimal(12,4) NOT NULL,
  `produto_servico_calculo_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `servicos`
--

CREATE TABLE `servicos` (
  `id` binary(16) NOT NULL,
  `nome` varchar(140) NOT NULL,
  `preco` decimal(12,2) NOT NULL,
  `unidade` enum('M','M2','M3','UN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `servicos`
--

INSERT INTO `servicos` (`id`, `nome`, `preco`, `unidade`) VALUES
(0x09105b05696347c7a1339bc972ca69b3, 'PROCESSO DE IMPRESSÃO', 8.50, 'M2'),
(0x353ec9549ad64e48b0bb33f56754ebf5, 'ILHOSAGEM', 2.00, 'UN'),
(0x3c208e95c9ec40d0acc38de1ec8d2678, 'REFILE ADESIVO', 0.50, 'M'),
(0x6d0cc9d0ff2e4520ae9f69bd1d9977b7, 'MONTAGEM DE BANNER', 5.00, 'M2'),
(0x78fbd815a09f438399f63b84d96b3736, 'RECORTE CONTORNO', 1.00, 'M'),
(0xa30b09ab8cfe44a8a452e676630a6110, 'DEPILE', 0.50, 'M2'),
(0xa7ef719ce50f411c9fbb90361ddacf7f, 'EMBAINHAGEM', 2.00, 'M'),
(0xc4b17d30a0f94473ad86b8538fd22c5f, 'RECORTE RETO', 0.50, 'M'),
(0xc5e7e7e43aa745a1abfa216463b2577d, 'AJUSTE DE ARTE SIMPLES', 5.00, 'UN'),
(0xd10e647063244ca7b45ed854ef44eb1b, 'REFILE LONA', 1.00, 'M'),
(0xd16c8484ec8f4961a8f4d692b0494fb7, 'RECORTE INTERNO', 1.00, 'M'),
(0xf548702d1edd4b7096fe2c29f5e6d655, 'SOLDAGEM DE BANNER', 3.00, 'M');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `pessoa_id` binary(16) NOT NULL,
  `senha` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `usuarios`
--

INSERT INTO `usuarios` (`pessoa_id`, `senha`) VALUES
(0x22541355a59b4202959246ed0608391f, '$2a$10$hdEONSqJL/JVN1IWRKMBruPM3NkVcGwzf5l1LMZHCoFCx.FeQJNBS'),
(0xa6662d2514e54ac698b94c9ef0176de8, '$2a$10$b5Fb05bzO4u0hhUHkWZsOu7mbIwh38Fz94FYwP.r/V6gwds/5AwnG');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario_permissoes`
--

CREATE TABLE `usuario_permissoes` (
  `id` binary(16) NOT NULL,
  `nome` varchar(60) NOT NULL,
  `usuario_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Despejando dados para a tabela `usuario_permissoes`
--

INSERT INTO `usuario_permissoes` (`id`, `nome`, `usuario_id`) VALUES
(0x035f51d6fe0e4dcabcbdbdd376dd706a, 'grupos', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x0a0a612ec9d644b3bc9c4357df4427e0, 'parceiros', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x1fee63e566714e5ea42fff9f4cae5377, 'fornecedores', 0x22541355a59b4202959246ed0608391f),
(0x23980cecb1ba49c7bec53ca125000901, 'usuarios', 0x22541355a59b4202959246ed0608391f),
(0x2779507b6b8644d8b382c8df43e772c6, 'colaboradores', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x39bcfc12e7ef4a93858a5e7d80b4fc85, 'parceiros', 0x22541355a59b4202959246ed0608391f),
(0x5a8057df4e464a688d2170e5beabeb72, 'clientes', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x6b97dd69f0c84838aea017721dbd5c8f, 'usuarios', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x7b9aaf15913047ff9c99acb5967231a5, 'servicos', 0xa6662d2514e54ac698b94c9ef0176de8),
(0x82b7581e4d7b45fe84162efd867596b6, 'clientes', 0x22541355a59b4202959246ed0608391f),
(0xae422694983440acb517286c3ae3536b, 'fornecedores', 0xa6662d2514e54ac698b94c9ef0176de8),
(0xc096df6a89714c0aa578885dc60d2ce5, 'colaboradores', 0x22541355a59b4202959246ed0608391f),
(0xeeb3a58fb4ba41acb7e70066fdf710e2, 'materias', 0xa6662d2514e54ac698b94c9ef0176de8),
(0xf8445711fd7a4a298f0156f79d5b8038, 'produtos', 0xa6662d2514e54ac698b94c9ef0176de8);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `calculos`
--
ALTER TABLE `calculos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfyaqn2h6qujsokv9p6aot74cg` (`nome`);

--
-- Índices de tabela `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`pessoa_id`);

--
-- Índices de tabela `colaboradores`
--
ALTER TABLE `colaboradores`
  ADD PRIMARY KEY (`pessoa_id`),
  ADD UNIQUE KEY `UKcq30m5bi2y38yu1gltj4m5b33` (`ctps`);

--
-- Índices de tabela `fornecedores`
--
ALTER TABLE `fornecedores`
  ADD PRIMARY KEY (`pessoa_id`);

--
-- Índices de tabela `materias`
--
ALTER TABLE `materias`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKa9wyc5r1ij7qdtdgabar7jahw` (`nome`);

--
-- Índices de tabela `parceiros`
--
ALTER TABLE `parceiros`
  ADD PRIMARY KEY (`pessoa_id`);

--
-- Índices de tabela `pessoas`
--
ALTER TABLE `pessoas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKouqc5requard3nhnb6u3wvksm` (`email`),
  ADD UNIQUE KEY `UK1hyqm1lnm2mu33nk2q0nci39w` (`telefone`);

--
-- Índices de tabela `pessoas_fisica`
--
ALTER TABLE `pessoas_fisica`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2x6ayy2oxv32858xi3cdb6adq` (`cpf`),
  ADD UNIQUE KEY `UK92lrbtinwf616nce74m7s05e0` (`rg`);

--
-- Índices de tabela `pessoas_juridica`
--
ALTER TABLE `pessoas_juridica`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `produtos`
--
ALTER TABLE `produtos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK68les18ejq8cjyxw9snrbtd7t` (`nome`);

--
-- Índices de tabela `produtos_materias_calculos`
--
ALTER TABLE `produtos_materias_calculos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKkfquu73yp4ibnwwr7u54e1tbs` (`calculo_id`),
  ADD KEY `FK9bn9xxi7jn2r88hg69mxm6s99` (`materia_id`),
  ADD KEY `FKku3w3cai5gkglbs94dc665xh7` (`produto_id`);

--
-- Índices de tabela `produtos_materias_parametros_calculos`
--
ALTER TABLE `produtos_materias_parametros_calculos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKng2ik41lieitt03dntx3pkwfu` (`produto_materia_calculo_id`);

--
-- Índices de tabela `produtos_servicos_calculos`
--
ALTER TABLE `produtos_servicos_calculos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKsog2fituw7cxsut2gjjk3ayn3` (`calculo_id`),
  ADD KEY `FKtmahh8ewl02dmdvbea5hunk9b` (`produto_id`),
  ADD KEY `FKdaovxeyulrr0vy7qcig7dek5o` (`servico_id`);

--
-- Índices de tabela `produtos_servicos_parametros_calculos`
--
ALTER TABLE `produtos_servicos_parametros_calculos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK6f7f81wcramurc5enipyqlvmh` (`produto_servico_calculo_id`);

--
-- Índices de tabela `servicos`
--
ALTER TABLE `servicos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkb972hkbvdm429coc3qxrf2wp` (`nome`);

--
-- Índices de tabela `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`pessoa_id`);

--
-- Índices de tabela `usuario_permissoes`
--
ALTER TABLE `usuario_permissoes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK79a1ykfql84qaghkfcfqiqgjw` (`usuario_id`);

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `clientes`
--
ALTER TABLE `clientes`
  ADD CONSTRAINT `FKm132nsa4qiwx80nqajaobrich` FOREIGN KEY (`pessoa_id`) REFERENCES `pessoas` (`id`);

--
-- Restrições para tabelas `colaboradores`
--
ALTER TABLE `colaboradores`
  ADD CONSTRAINT `FK7jaf7ptlxdxc30u2h92c3tgbv` FOREIGN KEY (`pessoa_id`) REFERENCES `pessoas_fisica` (`id`);

--
-- Restrições para tabelas `fornecedores`
--
ALTER TABLE `fornecedores`
  ADD CONSTRAINT `FKs78a81proucspy34ssfkrq6cb` FOREIGN KEY (`pessoa_id`) REFERENCES `pessoas` (`id`);

--
-- Restrições para tabelas `parceiros`
--
ALTER TABLE `parceiros`
  ADD CONSTRAINT `FKr216ub715vfyo48u2d6hihasn` FOREIGN KEY (`pessoa_id`) REFERENCES `pessoas` (`id`);

--
-- Restrições para tabelas `pessoas_fisica`
--
ALTER TABLE `pessoas_fisica`
  ADD CONSTRAINT `FKsib63phegilraka1brbtruwu` FOREIGN KEY (`id`) REFERENCES `pessoas` (`id`);

--
-- Restrições para tabelas `pessoas_juridica`
--
ALTER TABLE `pessoas_juridica`
  ADD CONSTRAINT `FKr98863dnkb3t4fmgl3xn6qfh7` FOREIGN KEY (`id`) REFERENCES `pessoas` (`id`);

--
-- Restrições para tabelas `produtos_materias_calculos`
--
ALTER TABLE `produtos_materias_calculos`
  ADD CONSTRAINT `FK9bn9xxi7jn2r88hg69mxm6s99` FOREIGN KEY (`materia_id`) REFERENCES `materias` (`id`),
  ADD CONSTRAINT `FKkfquu73yp4ibnwwr7u54e1tbs` FOREIGN KEY (`calculo_id`) REFERENCES `calculos` (`id`),
  ADD CONSTRAINT `FKku3w3cai5gkglbs94dc665xh7` FOREIGN KEY (`produto_id`) REFERENCES `produtos` (`id`);

--
-- Restrições para tabelas `produtos_materias_parametros_calculos`
--
ALTER TABLE `produtos_materias_parametros_calculos`
  ADD CONSTRAINT `FKng2ik41lieitt03dntx3pkwfu` FOREIGN KEY (`produto_materia_calculo_id`) REFERENCES `produtos_materias_calculos` (`id`);

--
-- Restrições para tabelas `produtos_servicos_calculos`
--
ALTER TABLE `produtos_servicos_calculos`
  ADD CONSTRAINT `FKdaovxeyulrr0vy7qcig7dek5o` FOREIGN KEY (`servico_id`) REFERENCES `servicos` (`id`),
  ADD CONSTRAINT `FKsog2fituw7cxsut2gjjk3ayn3` FOREIGN KEY (`calculo_id`) REFERENCES `calculos` (`id`),
  ADD CONSTRAINT `FKtmahh8ewl02dmdvbea5hunk9b` FOREIGN KEY (`produto_id`) REFERENCES `produtos` (`id`);

--
-- Restrições para tabelas `produtos_servicos_parametros_calculos`
--
ALTER TABLE `produtos_servicos_parametros_calculos`
  ADD CONSTRAINT `FK6f7f81wcramurc5enipyqlvmh` FOREIGN KEY (`produto_servico_calculo_id`) REFERENCES `produtos_servicos_calculos` (`id`);

--
-- Restrições para tabelas `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `FKqj68foxad8ueue00gdx66ufu` FOREIGN KEY (`pessoa_id`) REFERENCES `colaboradores` (`pessoa_id`);

--
-- Restrições para tabelas `usuario_permissoes`
--
ALTER TABLE `usuario_permissoes`
  ADD CONSTRAINT `FK79a1ykfql84qaghkfcfqiqgjw` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`pessoa_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
