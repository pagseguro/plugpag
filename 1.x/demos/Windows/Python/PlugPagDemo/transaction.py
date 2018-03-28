import re
import ctypes

"""
Class to implement payment routines.
"""
class Payment:

    METHOD = 1
    INSTALLMENTS = 1
    INSTALLMENT = 1
    USERREF = b'UserRef'

    MIN_VALUE = 1.0
    MAX_VALUE = 100000.0

    """
    Creates a new payment routine object.

    plugPag: PlugPag library instance reference.
    encoding: Charset used to encode/decode strings.
    """
    def __init__(self, plugPag, encoding):
        self._lib = plugPag
        self._encoding = encoding

    """
    Executes the payment transaction.

    transactionResult: TransactionResult to be filled with the transaction's result.
    return: Transaction status code.
    """
    def execute(self, transactionResult):
        amount = self.readValue()
        print('Aguardando pagamento...')
        ret = ctypes.c_int(self._lib.SimplePaymentTransaction(Payment.METHOD,
                                                              Payment.INSTALLMENTS,
                                                              Payment.INSTALLMENT, 
                                                              bytes(amount, self._encoding), 
                                                              Payment.USERREF,
                                                              ctypes.byref(transactionResult)))

        return ret

    """
    Reads the value to be paid.
    """
    def readValue(self):
        value = None

        while (value is None):
            readValue = input('Valor: R$')

            try:
                numValue = float(readValue)

                if numValue >= Payment.MIN_VALUE and numValue <= Payment.MAX_VALUE:
                    value = str(int(numValue * 100))
            except Exception:
                value = None

            if value is None:
                print('!!! Valor invalido !!!')

        return value


####################################################################################################


"""
Class to implement payment cancellation routines.
"""
class CancelPayment:

    """
    Creates a new payment cancellation routine object.

    plugPag: PlugPag library instance reference.
    """
    def __init__(self, plugPag):
        self._lib = plugPag

    """
    Executes the payment cancellation.

    transactionResult: TransactionResult to be filled with the transaction's result.
    return: Transaction status code.
    """
    def execute(self, transactionResult):
        print('Aguardando estorno...')
        return ctypes.c_int(self._lib.CancelTransaction(ctypes.byref(transactionResult)))


####################################################################################################


"""
Class to implement last transaction query routines.
"""
class QueryLastTransaction:

    """
    Creates a new transaction query routine object.

    plugPag: PlugPag library instance reference.
    """
    def __init__(self, plugPag):
        self._lib = plugPag

    """
    Executes the transaction query.

    transactionResult: TransactionResult to be filled with the transaction's result.
    return: Transaction status code.
    """
    def execute(self, transactionResult):
        print('Consultando ultima transacao...')
        return ctypes.c_int(self._lib.GetLastApprovedTransactionStatus(ctypes.byref(transactionResult)))