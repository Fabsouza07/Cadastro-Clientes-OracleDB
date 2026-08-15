# Checklist de Segurança — Remoção de Segredos e Variáveis de Ambiente

Este checklist contém ações práticas para remover segredos do repositório, evitar que novos segredos sejam comitados e configurar variáveis de ambiente de forma segura.

1. Remover segredos existentes no repositório

   - Inspecione o repositório por arquivos contendo credenciais (ex.: `config.properties`, `.env`, arquivos JSON/XML). Use busca por termos como `password`, `senha`, `token`, `secret`.
   - Se encontrar segredos em arquivos versionados, remova-os e substitua por placeholders. Exemplo: mantenha `config.properties.example` sem segredos.
   - Para remover segredos do histórico Git (recomenda-se o uso do `git filter-repo` ou BFG):

```bash
# Usando git filter-repo (recomendado)
git clone --mirror <repo-url> repo.git
cd repo.git
git filter-repo --path src/main/resources/config.properties --invert-paths
# revisar e forçar push para remoto (cuidado: reescreve histórico)
git push --force --mirror origin
```

```bash
# Usando BFG para remover arquivos com segredos
bfg --delete-files config.properties
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push --force
```

   - Após reescrever histórico, comunique a equipe para que todos re-clonem o repositório.

2. Não comitar arquivos com segredos

   - Adicione entradas relevantes ao `.gitignore` (ex.: `config.properties`, `*.env`, `secrets/`).
   - Commit apenas de `config.properties.example` com instruções claras.

3. Adote variáveis de ambiente para configurações sensíveis

   - Remova `db.senha` do arquivo versionado e carregue a senha via variável de ambiente: `ORACLE_DB_SENHA`.
   - Exemplos de export / set:

```bash
# Linux / macOS (bash)
export ORACLE_DB_URL="jdbc:oracle:thin:@//servidor:1521/servico"
export ORACLE_DB_USUARIO="cadastro_app"
export ORACLE_DB_SENHA="sua_senha_segura"

# Windows PowerShell
$env:ORACLE_DB_URL = 'jdbc:oracle:thin:@//servidor:1521/servico'
$env:ORACLE_DB_USUARIO = 'cadastro_app'
$env:ORACLE_DB_SENHA = 'sua_senha_segura'
```

   - No Java, prefira ler `System.getenv("ORACLE_DB_SENHA")` ou usar bibliotecas de configuração que suportem ambientes e profiles.

4. Configure secrets no CI/CD em vez de variáveis em texto

   - Armazene segredos nos mecanismos do provedor: GitHub Secrets, GitLab CI/CD variables, Azure Pipelines Library, etc.
   - Configure jobs para usar esses segredos e nunca imprimir valores sensíveis nos logs.

5. Verificação automática (pre-commit e scans)

   - Instale e configure hooks pre-commit para bloquear commits com padrões de segredos (ex.: `git-secrets`, `pre-commit` com `detect-secrets`).

```bash
# exemplo: instalar git-secrets (Linux)
git clone https://github.com/awslabs/git-secrets.git
cd git-secrets && sudo make install
git secrets --install
git secrets --register-aws --global
```

   - Adicione scanners no pipeline CI (TruffleHog, detect-secrets, gitleaks) e falhe o build se segredos forem detectados.

6. Rotação e invalidação de segredos comprometidos

   - Se um segredo foi exposto, roteie a mudança do segredo imediatamente (DB password, API key), e revogue tokens/credenciais antigas.
   - Documente o procedimento de rotação rapidamente acessível no repositório privado (ex.: `SECURITY_RUNBOOK.md`).

7. Minimizar privilégios e proteger credenciais em produção

   - Use contas de banco com privilégios mínimos (principio do menor privilégio).
   - Restrinja o acesso aos segredos no provedor (quem pode ler/editar).

8. Uso de secret managers (recomendado)

   - Para produção, use serviços gerenciados: HashiCorp Vault, AWS Secrets Manager, Azure Key Vault, Google Secret Manager.
   - Integre a aplicação para buscar segredos em tempo de execução ou use provedores de configuração que suportem vaults.

9. Permissões de arquivos locais

   - Garanta permissões mínimas nos arquivos que possam conter segredos em ambiente local (ex.: `chmod 600 config.properties` em *nix).

10. Auditoria e monitoramento

   - Habilite alertas e logs de acesso a segredos sensíveis (quando suportado pelo provider).
   - Agende scans periódicos no repositório para detectar regressões.

Checklist rápido (resumo de ações)

- [ ] Remover segredos do histórico Git (se houver)
- [ ] Atualizar `.gitignore` para ignorar arquivos sensíveis
- [ ] Substituir credenciais em arquivos por variáveis de ambiente
- [ ] Configurar secrets no CI/CD
- [ ] Instalar hooks pre-commit para bloquear segredos
- [ ] Adicionar scanners no pipeline (gitleaks/trufflehog/detect-secrets)
- [ ] Implementar rotação de segredos e documentar o runbook
- [ ] Considerar Vault/Secrets Manager para produção

---

Se quiser, posso:

- adicionar um `pre-commit` básico ao repositório;
- criar um workflow GitHub Actions que execute `mvn test` e rode `gitleaks`;
- ou gerar um `SECURITY_RUNBOOK.md` com passos de rotação.
