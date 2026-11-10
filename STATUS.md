# Status do projeto

Onde o projeto está agora: rode `scripts/status.sh` (ele lê o Git) e veja os pull requests fechados do repositório.

## Como rodar
- `scripts/dev.sh test` roda os testes (sobe o banco e carrega o `.env`)
- `scripts/dev.sh run` sobe o app

## Decisões e por quê
- **O perfil viaja dentro do token.** Assim a API não consulta o banco a cada chamada: ela só confere a assinatura e já sabe quem é e o que pode fazer. O custo é que o token é uma foto do login. Quando rebaixei um usuário de ADMIN para CUSTOMER, o token antigo continuou abrindo a rota de admin até vencer.
- **O login com e-mail inexistente também calcula um hash.** O BCrypt é lento de propósito (cerca de 0,1 s). Sem isso, um e-mail que não existe responderia em milissegundos, e alguém poderia medir o tempo para descobrir quais e-mails têm conta. Medi 0,102 s com senha errada e 0,101 s com e-mail inexistente, e as duas respostas são idênticas.
- **Os testes de integração usam `@Transactional`.** Eles gravam no banco de verdade, e o Spring desfaz o que cada teste gravou. Sem isso, na segunda rodada o cadastro de um e-mail que já existe voltaria 409 e o teste falharia.
- **O dono da conta vem do token, e não do corpo da requisição.** Se a API aceitasse o e-mail do dono no corpo, qualquer usuário logado poderia abrir contas no nome de outra pessoa. O token é assinado, então ninguém consegue trocar o e-mail dentro dele sem a assinatura deixar de bater, e a API pode confiar que o `sub` é de quem fez o login.
- **Conta de outra pessoa devolve 404, e não 403.** Se a resposta fosse 403, quem testasse ids saberia quais contas existem, mesmo sem poder abri-las. Com 404, a resposta é igual para uma conta que não existe e para uma conta de outra pessoa, então ninguém consegue descobrir quais ids existem testando números.

## Limitações conhecidas
- O token continua válido até vencer, mesmo se o perfil mudar no banco
- O 401 de token ausente não vem em JSON
- Os testes dependem do banco do Compose (Testcontainers entra depois)