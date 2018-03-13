
#ifndef _PPPAGSEGURO_H_
#define _PPPAGSEGURO_H_

#if !defined(IOS)
#pragma pack(push,1)
#endif

#ifdef __cplusplus
extern "C" {
#endif

#if defined(ANDROID) || defined(__linux__) || defined(IOS)
#define LIBRARY_API //JNIEXPORT
#else //ANDROID
#if COMPILING_DLL
#define LIBRARY_API __declspec(dllexport)
#else //COMPILING_DLL
#define LIBRARY_API __declspec(dllimport)
#endif //COMPILING_DLL
#endif //ANDROID


/*
	Codigos de retorno
*/
#define PPPS_RET_OK  0
#define PPPS_ERR_BASE  -1000
#define PPPS_BUFF_SIZE                 PPPS_ERR_BASE-1
#define PPPS_NULL_PTR                  PPPS_ERR_BASE-2
#define PPPS_POS_NOT_READY             PPPS_ERR_BASE-3
#define PPPS_TRANS_DENIED              PPPS_ERR_BASE-4
#define PPPS_DATA_INV_RESULT_MESSAGE   PPPS_ERR_BASE-5  // Buffer de resposta da transacao invalido ao obter as informacoes de resultado da transacao
#define PPPS_INV_AMOUNT_PARAM          PPPS_ERR_BASE-6
#define PPPS_INV_TOT_AMOUNT_PARAM      PPPS_ERR_BASE-7
#define PPPS_INV_USER_REF_PARAM        PPPS_ERR_BASE-8
#define PPPS_INV_TRS_RESULT_PARAM      PPPS_ERR_BASE-9  // Parametro invalido: Transaction Result
#define PPPS_DRIVER_NOT_FOUND          PPPS_ERR_BASE-10 // Nao encontrou dll de driver
#define PPPS_DRIVER_FUNCTION_ERROR     PPPS_ERR_BASE-11
#define PPPS_INV_FORMAT_AMOUNT_PARAM   PPPS_ERR_BASE-12
#define PPPS_INV_LEN_USER_REF_PARAM    PPPS_ERR_BASE-13
#define PPPS_INVALID_BUFFER            PPPS_ERR_BASE-14
#define PPPS_INV_APP_NAME_PARAM        PPPS_ERR_BASE-15
#define PPPS_INV_APP_VERSION_PARAM     PPPS_ERR_BASE-16
#define PPPS_APP_NAME_VERSION_NOT_SET  PPPS_ERR_BASE-17
#define PPPS_TRANS_NODATA              PPPS_ERR_BASE-18   // sem dados da transa��o
#define PPPS_COMMUNICATION_ERROR       PPPS_ERR_BASE-19
#define PPPS_SHARE_MODE_NOT_ALLOWED    PPPS_ERR_BASE-20

#define PPPS_ERR_UNKNOW          PPPS_ERR_BASE-999



/*
	Definicao de tamanhos dos campos da estrutura de dados de resultado de transacao
*/
#define PPPS_COMPORT_LEN 8 + 1
#define PPPS_ERROR_CODE_LEN   4 + 1
#define PPPS_MESSAGE_LEN  1023 + 1
#define PPPS_USER_REFERENCE_LEN   10 + 1
#define PPPS_AMOUNT_LEN   13 + 1
#define PPPS_TRS_CODE_LEN   32 + 1
#define PPPS_DATE_LEN   10 + 1
#define PPPS_TIME_LEN   8 + 1
#define PPPS_TRS_HOSTNSU_LEN   12 + 1
#define PPPS_CARD_BRAND_LEN   30 + 1
#define PPPS_BIN_LEN   6 + 1
#define PPPS_HOLDER_LEN   4 + 1
#define PPPS_RAW_BUFFER_LEN  65542 + 1
#define PPPS_TERMINAL_SERIAL_NUMBER_LEN 65 + 1
#define PPPS_APP_NAME_LEN 25 + 1
#define PPPS_APP_VERSION_LEN 10 + 1

/*
	Definicao do tipos dos parametros
*/
typedef enum {
	PPPAGSEGURO_CREDIT = 1,
	PPPAGSEGURO_DEBIT = 2,
	PPPAGSEGURO_VOUCHER = 3
} enPPPSPaymentMethod;


typedef enum {
	PPPAGSEGURO_A_VISTA = 1,
	PPPAGSEGURO_PARC_VENDEDOR = 2
} enPPPSInstallmentType;

typedef char tyComPort [PPPS_COMPORT_LEN];
typedef char tyAmount [PPPS_TRS_CODE_LEN];
typedef char tyUserReference [PPPS_USER_REFERENCE_LEN];
typedef char tyAppName[PPPS_APP_NAME_LEN];
typedef char tyAppVersion[PPPS_APP_VERSION_LEN];

/*
	Definicao da estrutura de dados de resultado da transacao
*/
typedef struct {
	char rawBuffer [PPPS_RAW_BUFFER_LEN];
	char message  [PPPS_MESSAGE_LEN];
	char transactionCode  [PPPS_TRS_CODE_LEN];
	char date  [PPPS_DATE_LEN];
	char time  [PPPS_TIME_LEN];
	char hostNsu  [PPPS_TRS_HOSTNSU_LEN];
	char cardBrand  [PPPS_CARD_BRAND_LEN];
	char bin  [PPPS_BIN_LEN];
	char holder [PPPS_HOLDER_LEN];
	char userReference [PPPS_USER_REFERENCE_LEN];
    char terminalSerialNumber [PPPS_TERMINAL_SERIAL_NUMBER_LEN];
} stPPPSTransactionResult;



/*
	GetVersionLib
	Retorna uma string null terminated com a vers�o da biblioteca de integracao

	Parametros: nenhum

	Retorno:
		const char*  -  string null terminated com a versao da biblioteca de integracao
*/
LIBRARY_API
const char *GetVersionLib (void);


/*
	InitBTConnection
	Configura a porta com que est� pareada com a Moderninha

	Parametros:
		const char*  comport  -  Porta COM mapeada para Bluetooth e ja pareada com a Moderninha

	Retorno: nenhum
*/
LIBRARY_API
int InitBTConnection (const tyComPort* comport);


/*
	SimplePaymentTransaction
	Inicia a transacao de venda. Em caso de sucesso, retorna os dados da transacao numa estrutura

	Parametros:
		enPPPSTansType transtype  -  Tipo de transacao, credito, debito, voucher
		enPPPSInstallmentType installmenttype  -  Tipo de parcelamento, a vista, parcelado
		const char* amount  -  Valor da transacao, com 2 para centavos, sem pontos e virgulas. Ex: "R$ 1.234,56" deve ser passado como "123456"
		unsigned int installments  -  Numero de parcelas. Caso a vista, valor deve ser 1
		const char* userreference  -  Codigo de venda, definido pelo aplicativo
		stPPPSTransactionResult* transactionResult  -  Estrutura com os dados de resultado da transacao

	Retorno:
		int  -  De acordo com a lista de codigos de retorno possiveis
*/
LIBRARY_API
int SimplePaymentTransaction (
	enPPPSPaymentMethod paymentMethod,
	enPPPSInstallmentType installmentType,
	unsigned int installments,
	const tyAmount* amount,
	const tyUserReference* userreference,
	stPPPSTransactionResult* transactionResult
);



/*
	CancelTransaction
	Inicia a transacao de estorno. Em caso de sucesso, retorna os dados da transacao numa estrutura

	Parametros:
		stPPPSTransactionResult* transactionResult  -  Estrutura com os dados de resultado da transacao

	Retorno:
		int  -  De acordo com a lista de codigos de retorno possiveis
*/
LIBRARY_API
int CancelTransaction (stPPPSTransactionResult* transactionResult);


/*
	GetLastTransactionStatus
	Inicia a transacao de consulta da ultima transacao realizada com sucesso.
	Em caso de sucesso, retorna os dados da transacao numa estrutura

	Parametros:
		stPPPSTransactionResult* transactionResult  -  Estrutura com os dados de resultado da transacao

	Retorno:
		int  -  De acordo com a lista de codigos de retorno possiveis
*/
LIBRARY_API
int GetLastApprovedTransactionStatus (stPPPSTransactionResult* transactionResult);


LIBRARY_API
void UnloadDriverConnection(void);

LIBRARY_API
    void InitIOSDummyConnection(void);
    
LIBRARY_API
int SetVersionName(tyAppName appName, tyAppVersion version);

#ifdef __cplusplus
}
#endif

#if !defined(IOS)
#pragma pack(pop)
#endif

#endif // _PPPAGSEGURO_H_
