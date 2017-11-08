#-*- coding: UTF-8 -*-

import ctypes
import transaction

APP_NAME = b'PlugPagPython'
APP_VERSION = b'1.0.0'
BLUETOOTH_PORT = b'COM12'

ENCODING = 'utf-8'

MENU_OPTIONS = [(1, 'Pagar'),
                (2, 'Estornar (cancelar pagamento)'),
                (3, 'Consultar ultima transacao')]


"""
Transaction result structure.
"""
class TransactionResult(ctypes.Structure):
    _fields_ = [('rawBuffer', ctypes.ARRAY(65543, ctypes.c_char)),
                ('message', ctypes.ARRAY(1024, ctypes.c_char)),
                ('transactionCode', ctypes.ARRAY(33, ctypes.c_char)),
                ('date', ctypes.ARRAY(11, ctypes.c_char)),
                ('time', ctypes.ARRAY(9, ctypes.c_char)),
                ('hostNsu', ctypes.ARRAY(13, ctypes.c_char)),
                ('cardBrand', ctypes.ARRAY(31, ctypes.c_char)),
                ('bin', ctypes.ARRAY(7, ctypes.c_char)),
                ('holder', ctypes.ARRAY(5, ctypes.c_char)),
                ('userReference', ctypes.ARRAY(11, ctypes.c_char)),
                ('terminalSerialNumber', ctypes.ARRAY(66, ctypes.c_char))]



"""
Loads the DLLs and returns the DLLs references to call PlugPag methods.
"""
def loadLibraries():
    ppLib = ctypes.cdll.LoadLibrary('lib/PPPagSeguro.dll')
    ctypes.cdll.LoadLibrary('lib/BTSerial.dll')
    ctypes.cdll.LoadLibrary('lib/PlugPag.dll')

    return ppLib



"""
Initializes PlugPag parameters to allow transactions.
"""
def initPlugPag(pagSeguroLib):
    print('Definindo nome e versao da aplicacao... ', end = '')
    pagSeguroLib.SetVersionName(APP_NAME, APP_VERSION)
    print('OK')

    print('Configurando conexao bluetooth... ', end = '')
    pagSeguroLib.InitBTConnection(BLUETOOTH_PORT)
    print('OK')



"""
Prints a transaction's result.
"""
def printResult(resultCode, transactionResult):
    print('+-------------------------------------------------------------')

    if resultCode.value != 0:
        print('| Result:  {}'.format(resultCode.value))

    if len(transactionResult.message) > 0:
        print('| Message: {}'.format(transactionResult.message.decode(ENCODING)))

    print('+-------------------------------------------------------------')

    if resultCode.value == 0:
        print('| Transaction code:       {}'.format(transactionResult.transactionCode.decode(ENCODING)))
        print('| Date:                   {}'.format(transactionResult.date.decode(ENCODING)))
        print('| Time:                   {}'.format(transactionResult.time.decode(ENCODING)))
        print('| Host NSU:               {}'.format(transactionResult.hostNsu.decode(ENCODING)))
        print('| Card brand:             {}'.format(transactionResult.cardBrand.decode(ENCODING)))
        print('| Bin:                    {}'.format(transactionResult.bin.decode(ENCODING)))
        print('| Holder:                 {}'.format(transactionResult.holder.decode(ENCODING)))
        print('| User reference:         {}'.format(transactionResult.userReference.decode(ENCODING)))
        print('| Terminal serial number: {}'.format(transactionResult.terminalSerialNumber.decode(ENCODING)))
        print('+-------------------------------------------------------------')



"""
Prints the menu.
"""
def printMenu():
    menuOptions = [(1, 'Pagar'),
                   (2, 'Estornar (cancelar pagamento)'),
                   (3, 'Consultar ultima transacao')]

    print('\n:: MENU')
    for entry in MENU_OPTIONS: print('{}. {}'.format(entry[0], entry[1]))

"""
Reads the action to be taken.
"""
def readOption():
    isOptionValid = False

    while (isOptionValid is False):
        printMenu()

        try:
            option = int(input('>> '))

            if option in range(1, len(MENU_OPTIONS) + 1):
                isOptionValid = True
        except Exception:
            isOptionValid = False

        if (isOptionValid is False):
            print('!!! Opcao invalida !!!')

    return option


"""
Main method.
"""
def main():
    # Initialize PlugPag
    pp = loadLibraries()
    initPlugPag(pp)

    # Instructions to quit
    print("\n\n*** Pressione Ctrl+C para finalizar a aplicacao ***")

    try:
        while (True):
            # Read menu option
            option = readOption()

            # Handle selected option
            returnValue = None
            transactionResult = TransactionResult()

            print('')

            if option == 1:
                # Payment option
                returnValue = transaction.Payment(pp, ENCODING).execute(transactionResult)
            elif option == 2:
                # Cancel transaction
                returnValue = transaction.CancelPayment(pp).execute(transactionResult)
            elif option == 3:
                # Query last transaction
                returnValue = transaction.QueryLastTransaction(pp).execute(transactionResult)

            if returnValue is not None:
                printResult(returnValue, transactionResult)
    except KeyboardInterrupt:
        print("\n\nFIM\n..........")



if __name__ == '__main__':
    print("""
    Aplicacao de demonstracao da biblioteca PlugPag com integracao para Python rodando no sistema
    operation Windows.
    Testado no Windows 10, com Python 32 bits.
    """)
    main()
