# PagCafe

## Objetivo

Essa aplicação tem como finalidade servir como exemplo de uso da biblioteca PlugPag para efetuar transações com os terminais compatíveis.
Essa aplicação simula um ambiente de venda de cafés. Cada café é vendido ao valor de R$2,65.

## Funcionalidades

A aplicação possui 3 funcionalidades: **Fazer uma venda**, **Fazer estorno** e **Consultar última venda**.

### Fazer uma venda
* Inicia uma nova `Activity` com opções para pagamento.
* **Crédito** / **Débito**: Seleção do tipo de pagamento. O valor padrão é Crédito
* **+** / **-**: Alteram a quantidade de cafés a serem vendidos. Não é possível vender menos de 1 café.
* **Pagar**: Inicia a transação com o terminal de pagamento. A mensagem "Aguarde (n)" será exibida enquanto a aplicação aguarda uma resposta do terminal. O número _n_ indica o número da tentativa de transação enviada ao terminal.
* Ao final da operação, é exibido seu status em um `DialogFragment`. Há três exibições diferentes:
    * Exibe o erro da transação quando ocorrer um erro não tratado.
    * Exibe uma mensagem de comparação de transações (venda atual e última venda realizada com sucesso) quando houver um erro tratado durante a transação.
    * Exibe informações da venda quando a transação ocorre sem erros.

### Fazer estorno
* Inicia comunicação com o terminal, solicitando estorno de venda.
* Ao final da operação, são exibidas informações sobre o estorno em um `DialogFragment`.
* O usuário deve clicar no botão "OK" para fechar o `DialogFragment` e retornar à tela anterior.

### Consultar última venda
* Inicia comunicação com o terminal, solicitando os dados da última transação efetuada com sucesso.
* Ao final da operação, são exibidas informações sobre a última transação efetuada com sucesso em um `DialogFragment`.
* O usuário deve clicar no botão "OK" para fechar o `DialogFragment` e retornar à tela anterior.

## Resultados

Após cada operação, a tela de resultados é exibida.
Essa tela pode apresentar um erro ou um sucesso.

* **Erro**: Exibe a mensagem *"Erro: X"*, onde *X* é um código do erro. Caso o erro possua mais informações, essas informações são exibidas abaixo.
* **Sucesso**: Exibe informações sobre a venda.
    * **Transaction code**: Código da transação.
    * **Date**: Data da venda.
    * **Time**: Horário da venda.
    * **Host NSU**: NSU (Número Sequencial Único) do terminal.
    * **Card Brand**: Bandeira do cartão utilizado.
    * **Bin**: Número de identificação do banco (Bank Identification Number).
    * **Holder**: 4 (quatro) últimos dígitos do número do cartão.
    * **User Reference**: Nome da aplicação que efetuou o pagamento.
    * **Serial Number**: Número de série do terminal no qual o pagamento foi efetuado.
