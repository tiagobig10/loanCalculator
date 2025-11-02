## Autor
Pedro Tiago

## Demo
https://d159modh3rr22x.cloudfront.net/api/v1/loan-calculator

## Endpoint
### `POST /api/v1/loan-calculator`

Serviço de Cálculo de Empréstimos

## Tecnologias
Este projeto foi desenvolvido utilizando **Java 17** com **Spring Boot 3** .

## Detalhes da Implementação

**Método:** `POST`

**URL:** `/api/v1/loan-calculator`

**Tipo de Conteúdo:** `application/json`

### Request Body

O corpo da requisição deve ser um objeto JSON.

| Campo | Tipo | Descrição | Obrigatório | Exemplo |
| :--- | :--- | :--- | :--- | :--- |
| `startDate` | `LocalDate` | A data de início do cálculo | Sim | `2024-01-01` |
| `endDate` | `LocalDate` | A data final do cálculo  | Sim | `2034-01-01` |
| `firstPayment` | `LocalDate` |  A data do primeiro pagamento de parcela  | Sim | `2024-01-15` |
| `loanAmount` | `Long` |  O valor do empréstimo  | Sim | `14000000` |
| `interestRate` | `Interger` |  A taxa de juros em porcentagem a ser aplicada no empréstimo  | Sim | `7` |

**Exemplo de Requisição:**

```json
{
    "startDate": "2024-01-01",
    "endDate": "2034-01-01",
    "firstPayment": "2024-02-15",
    "loanAmount": 14000000,
    "interestRate": 7
}
```

## Testes Unitários
| Function | Descrição |
| :--- | :--- |
| `calculateInterest_ShouldCalculateCorrectlyFor30Days` | Deve calcular os juros corretamente para 30 dias a 10% a.a |
| `validateDates_ShouldThrowExceptionWhenEndDateIsNotAfterStartDate` | Deve lançar exceção quando a data final não for depois da data inicial. |
