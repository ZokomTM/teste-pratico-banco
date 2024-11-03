$apiUrl = "http://localhost:8080/api"
$headers = @{
    "Content-Type" = "application/json"
}

## CRIA A CONTA
function CriarConta {
    param (
        [string]$nome,
        [Double]$initialBalance,
        [string]$senha
    )

    $body = @{
        "clientName"  = $nome
        "initialBalance" = $initialBalance
        "password" = $senha
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$apiUrl/accounts" -Method Post -Headers $headers -Body $body
    return $response
}

## EFETUA LOGIN NA CONTA E PEGA TOKEN
function AutenticarConta {
    param (
        [string]$numeroConta,
        [string]$senha
    )

    $body = @{
        "accountNumber" = $numeroConta
        "password" = $senha
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$apiUrl/accounts/authenticate" -Method Post -Headers $headers -Body $body
    return $response.token
}

## CHAMADA PARA LISTAR TODAS AS CONTAS, APENAS PARA DEMONSTRAR A LISTAGEM COM CONTAS E TRANSAÇÕES
function ListAllAccount {
    $response = Invoke-RestMethod -Uri "$apiUrl/accounts" -Method Get
    return $response
}

## CRIA DUAS CONTAS E FAZ LOGIN PARA PEGAR TOKEN
$conta1 = CriarConta -nome "Usuario Teste 1" -initialBalance 10.0 -senha "senhaSegura123"
$conta2 = CriarConta -nome "Usuario Teste 2" -initialBalance 0.0 -senha "senhaSegura123"
$token1 = AutenticarConta -numeroConta $conta1.accountNumber -senha "senhaSegura123"
$token2 = AutenticarConta -numeroConta $conta2.accountNumber -senha "senhaSegura123"

Write-Output $conta.accountNumber
Write-Output $token1
Write-Output $token2

## AQUI PODE SER ADICIONADO NESSE MESMO FORMATO PARA TESTAR SE NO FINAL VALOR FICARÁ CORRETO APÓS DIVERSAS OPERAÇÕES
$movimentacoesConta1 = @(
    @{ accountNumber = $conta1.accountNumber; amount = 100.0; tipo = "deposit" },
    @{ accountNumber = $conta1.accountNumber; amount = 50.0; tipo = "withdraw" },
    @{ accountNumber = $conta1.accountNumber; amount = 200.0; tipo = "deposit" },
    @{ accountNumber = $conta1.accountNumber; amount = 350.0; tipo = "withdraw" },
    @{ accountNumber = $conta1.accountNumber; fromAccount = $conta1.accountNumber; toAccount = $conta2.accountNumber;  amount = 15.0; tipo = "transfer" },
    @{ accountNumber = $conta1.accountNumber; amount = 50.0; tipo = "withdraw" }
)

## ADICIONA VÁRIAS REQUISIÇÕES AO MESMO TEMPO PARA A CONTA 1, ISSO DEMONSTRA QUE SISTEMA DE FILA PARA REQUISIÇÕES ESTÁ FUNCIONANDO CORRETAMENTE
$jobs1 = @()
foreach ($movimentacao in $movimentacoesConta1) {
    $job = Start-Job -ScriptBlock {
        param ($token1, $movimentacao, $headers, $apiUrl)

        $headers["Authorization"] = "Bearer $token1"
        $body = $movimentacao | ConvertTo-Json

        $tipo = $movimentacao.tipo

        Invoke-RestMethod -Uri "$apiUrl/transactions/$tipo" -Method Post -Headers $headers -Body $body
    } -ArgumentList $token1, $movimentacao, $headers, $apiUrl

    $jobs1 += $job
}

$jobs1 | ForEach-Object {
    Wait-Job -Job $_
    Receive-Job -Job $_
    Remove-Job -Job $_
}

## AQUI PODE SER ADICIONADO NESSE MESMO FORMATO PARA TESTAR SE NO FINAL VALOR FICARÁ CORRETO APÓS DIVERSAS OPERAÇÕES
$movimentacoesConta2 = @(
    @{ accountNumber = $conta2.accountNumber; amount = 100.0; tipo = "deposit" },
    @{ accountNumber = $conta2.accountNumber; amount = 50.0; tipo = "withdraw" },
    @{ accountNumber = $conta2.accountNumber; amount = 200.0; tipo = "deposit" },
    @{ accountNumber = $conta2.accountNumber; fromAccount = $conta2.accountNumber; toAccount = $conta1.accountNumber;  amount = 356.0; tipo = "transfer" },
    @{ accountNumber = $conta2.accountNumber; fromAccount = $conta2.accountNumber; toAccount = $conta1.accountNumber;  amount = 56.0; tipo = "transfer" }
)

## ADICIONA VÁRIAS REQUISIÇÕES AO MESMO TEMPO PARA A CONTA 2, ISSO DEMONSTRA QUE SISTEMA DE FILA PARA REQUISIÇÕES ESTÁ FUNCIONANDO CORRETAMENTE
$jobs2 = @()
foreach ($movimentacao in $movimentacoesConta2) {
    $job = Start-Job -ScriptBlock {
        param ($token2, $movimentacao, $headers, $apiUrl)

        $headers["Authorization"] = "Bearer $token2"
        $body = $movimentacao | ConvertTo-Json

        $tipo = $movimentacao.tipo

        Invoke-RestMethod -Uri "$apiUrl/transactions/$tipo" -Method Post -Headers $headers -Body $body
    } -ArgumentList $token2, $movimentacao, $headers, $apiUrl

    $jobs2 += $job
}

$jobs2 | ForEach-Object {
    Wait-Job -Job $_
    Receive-Job -Job $_
    Remove-Job -Job $_
}

Write-Output $jobs

$todasContas = ListAllAccount
Write-Output $todasContas











