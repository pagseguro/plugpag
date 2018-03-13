
#include "libPPPagSeguro/PPPagSeguro.h"

#include <stdlib.h>
#include <stdio.h>

stPPPSTransactionResult transactionResult;


void pResult (stPPPSTransactionResult transactionResult)
{
	printf ("\nTransaction Result\n\tmessage [%s]\n\ttransactionCode [%s]\n\tdate [%s]\n\ttime [%s]\n\thostNsu [%s]\n\tcardBrand [%s]\n\tbin [%s]\n\tholder [%s]\n\tuser reference [%s]\n",
			transactionResult.message,
			transactionResult.transactionCode,
			transactionResult.date,
			transactionResult.time,
			transactionResult.hostNsu,
			transactionResult.cardBrand,
			transactionResult.bin,
			transactionResult.holder,
			transactionResult.userReference);
}



int main (int argc, char* argv [])
{
	int ret;
	tyComPort comPort;
	enPPPSPaymentMethod paymentMethod;
	enPPPSInstallmentType installmentType;
	unsigned int installment;
	tyAmount amount;
	tyUserReference userReference;
	stPPPSTransactionResult transactionResult;
	tyAppName appName;
	tyAppVersion version;

	memset ((void*) comPort, 0, sizeof (tyComPort));
	memset ((void*) amount, 0, sizeof (tyAmount));
	memset ((void*) userReference, 0, sizeof (tyUserReference));


	printf ("***************************************************************\n");
	printf ("***                      INICIANDO APP: %s\n", GetVersionLib ());
	printf ("***************************************************************\n\n");

	printf ("\nMODE DE USO: \n\n%s PORTA_COM TRANS_TYPE INSTALLMENT_TYPE INSTALLMENT_NUMBER AMOUNT USER_REFERENCE\n", argv [0]);
	printf ("\n\n");
	printf ("PORTA_COM: COM1, COM2, etc\n");
	printf ("TRANS_TYPE: 1 (Credito), 2 (Debito) ou 3 (Voucher) \n");
	printf ("INSTALLMENT_TYPE: 1 (A vista), 2 (Parc. vendedor)\n");
	printf ("INSTALLMENT_NUMBER: Quantidade de parcelas\n");
	printf ("AMOUNT: Valor antes do juros (somente parcelado comprador)\n");
	printf ("USER_REFERENCE: Codigo de venda [1..10]\n");
	printf ("\n\nEx:\n");
	printf ("%s COM7 1 1 1 12345 ABC123\n", argv [0]);
	printf ("\n\n");

	strcpy(appName, "CommandPromptTest");
	strcpy(version, "0.0.1");
	SetVersionName(appName, version);

	if (argc > 2)
	{
		if (!memcmp (argv [2], "STATUS", 6) ||
			!memcmp (argv [2], "status", 6))
		{
			printf ("STATUS");
			InitBTConnection ((tyComPort*) argv [1]);
			ret = GetLastApprovedTransactionStatus(&transactionResult);
			printf ("\n\nRETORNO: %d\n", ret);
			pResult (transactionResult);
			return ret;
		}
		else if (!memcmp (argv [2], "ESTORNO", 7) ||
				 !memcmp (argv [2], "estorno", 7))
		{
			printf ("ESTORNO");
			InitBTConnection ((tyComPort*) argv [1]);
			ret = CancelTransaction (&transactionResult);
			printf ("\n\nRETORNO: %d\n", ret);
			pResult (transactionResult);
			return ret;
		}
	}


	if (argc > 1)
		memmove (comPort, argv [1], strlen (argv [1]));

	if (argc > 2)
		paymentMethod = atoi ((char*) argv [2]);

	if (argc > 3)
		installmentType = atoi ((char*) argv [3]);

	if (argc > 4)
		installment = atoi ((char*) argv [4]);

	if (argc > 5)
		memmove (amount, argv [5], strlen (argv [5]));

	if (argc > 6)
		memmove (userReference, argv [6], strlen (argv [6]));

	printf ("VENDA");
	InitBTConnection ((tyComPort*) argv [1]);
	ret = SimplePaymentTransaction (
		paymentMethod,   // transaction type
		installmentType,   // installment type
		installment, // instalment number
		amount, //amount
		userReference,    // user reference (Codigo de Venda)
		&transactionResult
	);
	printf ("\n\nRETORNO: %d\n", ret);
	pResult (transactionResult);


	return ret;
}
