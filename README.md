# openpmo-plugin-agreement-goves

Plugin do OpenPMO para consultar Contratos e Convênios do GOVES por meio do Pentaho BI.

## Objetivo

Este projeto implementa `IAgreementProvider`, definido em `openpmo-plugin-agreement-interface`, e concentra autenticação Basic, chamadas ao Pentaho CDA, interpretação das linhas retornadas e conversão para os DTOs neutros do OpenPMO.

## Funcionalidades

- listar anos de Contratos ou Convênios;
- listar órgãos por tipo e ano;
- listar instrumentos por órgão;
- consultar os detalhes de um instrumento;
- converter entidades HTML presentes no retorno do Pentaho;
- carregar automaticamente o provider pelo Spring Boot.

## Requisitos

- Java 11 ou superior;
- Spring Boot 2.2.12;
- acesso ao Pentaho BI da SEP;
- JitPack configurado no projeto consumidor.

## Instalação

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.sep-es-br:openpmo-plugin-agreement-interface:1.0.5'
    implementation 'com.github.sep-es-br:openpmo-plugin-agreement-goves:1.0.9'
}
```

## Uso no OpenPMO API

```properties
app.agreement.parser.repository=com.github.sep-es-br:openpmo-plugin-agreement-goves:1.0.9
```

O contrato permanece como dependência fixa da aplicação. A coordenada acima controla a inclusão da implementação GOVES.

## Configuração

As configurações possuem o prefixo `pentaho-bi`.

| Propriedade | Padrão | Descrição |
| --- | --- | --- |
| `pentaho-bi.base-url` | `https://bi.sep.es.gov.br` | URL base do Pentaho. |
| `pentaho-bi.user-id` | `PENTAHO_BI_USER_ID` ou `pentahoBI.userId` | Usuário da autenticação Basic. |
| `pentaho-bi.password` | `PENTAHO_BI_PASSWORD` ou `pentahoBI.password` | Senha da autenticação Basic. |

Recomenda-se configurar as credenciais por variáveis de ambiente:

```powershell
$env:PENTAHO_BI_USER_ID='usuario'
$env:PENTAHO_BI_PASSWORD='senha'
```

Não grave credenciais reais no repositório.

### Consultas configuráveis

Cada consulta possui uma propriedade `*-path` e uma `*-data-access-id`:

| Grupo | Prefixo das propriedades |
| --- | --- |
| Anos de Convênios | `pentaho-bi.cooperation-years-*` |
| Órgãos de Convênios | `pentaho-bi.cooperation-organizations-*` |
| Processos de Convênios | `pentaho-bi.cooperation-processes-*` |
| Detalhe de Convênio | `pentaho-bi.cooperation-agreement-*` |
| Anos de Contratos | `pentaho-bi.contract-years-*` |
| Órgãos de Contratos | `pentaho-bi.contract-organizations-*` |
| Processos de Contratos | `pentaho-bi.contract-processes-*` |
| Detalhe de Contrato | `pentaho-bi.contract-agreement-*` |

Os valores padrão ficam em `agreement-parser.properties` e apontam para os arquivos CDA em `/public/dashboard/pmo/dados_abertos`.

## Auto-configuração Spring Boot

`META-INF/spring.factories` registra `AgreementParserAutoConfig`. Ao manter o plugin no classpath, o Spring Boot:

- carrega `agreement-parser.properties`;
- registra `PentahoAgreementProperties`;
- encontra `PentahoAgreementProvider` por component scan;
- disponibiliza o provider como bean de `IAgreementProvider`.

Não é necessário usar `@Import` manualmente.

## Uso pelo contrato

```java
@Service
public class AgreementIntegrationService {

    private final IAgreementProvider provider;

    public AgreementIntegrationService(final IAgreementProvider provider) {
        this.provider = provider;
    }

    public List<AgreementDto> findContracts(
        final Long year,
        final AgreementOrganizationDto organization
    ) {
        return provider.getAgreements(
            AgreementType.CONTRACT,
            year,
            organization
        );
    }
}
```

## Chamadas ao Pentaho CDA

Todas as operações executam `GET /pentaho/plugin/cda/api/doQuery` com `path`, `dataAccessId` e os parâmetros da consulta.

| Operação | Parâmetros adicionais |
| --- | --- |
| Listar anos | nenhum |
| Listar órgãos | `paramp_ano` |
| Listar Contratos | `paramp_ano`, `paramp_orgao` |
| Listar Convênios | `paramp_ano`, `paramp_cod_ug` |
| Detalhar Contrato | `paramp_cod_origem` |
| Detalhar Convênio | `paramp_cod_conv` |

Quando a consulta de detalhe não retorna linhas, o provider retorna `null`. Credenciais ausentes geram `IllegalStateException`; falhas HTTP são propagadas pelo `WebClient`.

## Build local

```powershell
.\gradlew.bat clean build publishToMavenLocal
```

```bash
./gradlew clean build publishToMavenLocal
```

Para testar uma versão ainda não publicada do contrato, publique primeiro `openpmo-plugin-agreement-interface` no Maven local.
