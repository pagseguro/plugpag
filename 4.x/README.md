# PlugPag 4.x

## Controle de versão

|Versão Documento| Data       | Versão PlugPag |
|-|------------|---------------|
|`1.0.0`| 13/08/2025 | 4.12.0-beta   |

## Documentacões relacionadas
Para ver as descrições de classe e métodos, códigos de erro e exemplos acesso o seguinte documento: https://github.com/pagseguro/plugpag/blob/master/4.x/documento/Manual%20de%20Integra%C3%A7%C3%A3o%20-%20PlugPag%20Android%20Integradores.pdf.

**Observações Importantes:**
- A biblioteca PlugPag possui suporte da API level 21 (Android 5.0) em diante, devido a restrições de protocolos de comunicação;
- Apenas uma única instância do PlugPag deve existir durante o uso do aplicativo. A existência de múltiplas instâncias pode fazer com que o comportamento seja indeterminado.
- Por utilizar bluetooth para comunicação, o dispositivo Android não deve estar muito distante do terminal ou leitor.
- As chamadas dos métodos da classe PlugPag devem ser feitas em uma Thread que execute em background pois podem demorar para finalizar a execução. Caso a execução seja feita na Thread principal (UI Thread), o aplicativo pode apresentar um ANR (Application Not Responding). Além disso, alguns métodos executam transações utilizando chamadas remotas pela internet, o que impossibilita suas chamadas na Thread principal.
- Não é possível fazer chamadas da biblioteca caso o usuário tenha permissões de root no aparelho por motivos de segurança.
- O parâmetro userReference da classe PlugPagPaymentData deve possuir menos de 10 caracteres;

## Versão 4.12.0-beta
- Modificação do sistema de autenticação da PlugPag para um mais atualizado;
- Descontinuado o suporte para dispositivos M30;
- Atualização da versão do Java;
- Descontinuando suporte para versões do Android abaixo do Android 5.0.

#### Changelog

**Modificações na biblioteca PlugPag:**

- Atualização da biblioteca para o Java 17;
- Atualização do AGP (gradle) para a versão 8.6.0;
- Atualização do minSdk para a API Level 21.

**Modificações no Demo necessárias para usar a biblioteca:**

- Adição do repositório jitpack.io;
- Atualização do kotlin-gradle-plugin para a versão 1.9.0;
- Atualizado do tools.gradle para a versão 8.4.0;

```groovy
buildscript {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.4.0"
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0'
    }
}
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- Modificar versão do Java para o Java 17;
- Modificar o compileSDK e targetSDK para API 33 ou acima;

```groovy
android {
    compileSdk 34

    defaultConfig {
        ...
        targetSdk 33
        ...
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17
    }
    ...
}
```

- Adição de 3 novas permissões no AndroidManifest para usar com o Android Tiramisu (API Level 13) e acima.

```xml
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```