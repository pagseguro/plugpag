# Aplicação de demonstração do PlugPag com Java para desktops

Para executar a aplicação, faça o clone do repositório, navegue até o 
diretório da aplicação e execute o seguinte comando:

~~~
./gradlew jfxRun
~~~

Essa aplicação utiliza um arquivo `.jar` para criar uma interface entre a
linguagem Java e as bibliotecas do PlugPag, que foram escritas em `C`.

Atualmente a aplicação está pronta apenas para ser executada no sistema
operacional Windows.

A implementação da aplicação e seus testes foram realizados com o seguinte
ambiente:

* Windows 10 (64 bits)
* Java 1.8.0_151 (64 bits)
* Gradle 4.2
* IntelliJ IDEA 2017.2.5

Durante a execução da aplicação, algumas informações são impressas no 
console para que seja possível acompanhar as tarefas que estão sendo
executadas.