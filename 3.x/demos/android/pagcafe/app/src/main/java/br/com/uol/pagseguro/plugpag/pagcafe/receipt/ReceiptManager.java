package br.com.uol.pagseguro.plugpag.pagcafe.receipt;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.uol.pagseguro.plugpag.pagcafe.bluetooth.Bluetooth;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpag.PlugPagVoidData;

public final class ReceiptManager {

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------

    private static final String DIRECTORY = "PlugPagReceipts";

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    private static ReceiptManager sInstance = null;

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Context mContext = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new ReceiptManager.
     *
     * @param context Context used to access application data and device information.
     */
    private ReceiptManager(@Nullable Context context) {
        this.mContext = context;
    }

    // ---------------------------------------------------------------------------------------------
    // Singleton
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the singleton instance of the ReceiptManager.
     *
     * @return ReceiptManager singleton instance.
     */
    public static final ReceiptManager getInstance(@Nullable Context context) {
        if (ReceiptManager.sInstance == null) {
            ReceiptManager.sInstance = new ReceiptManager(context);
        }

        return ReceiptManager.sInstance;
    }

    // ---------------------------------------------------------------------------------------------
    // Save receipt
    // ---------------------------------------------------------------------------------------------

    /**
     * Saves a receipt's information.
     *
     * @param result Transaction result to be saved.
     * @return If the receipt has been successfully saved.
     * @throws IOException
     */
    public boolean saveReceipt(@NonNull PlugPagTransactionResult result) throws IOException {
        boolean saved = false;
        String path = null;
        File file = null;

        path = this.getFilePath(Bluetooth.getSelectedBluetoothDevice().getName());

        if (!TextUtils.isEmpty(path)) {
            file = new File(path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            this.write(file, result);
        }

        return saved;
    }

    /**
     * Writes a transaction result in the given file.
     *
     * @param file   File where the transaction data will be saved.
     * @param result Result to be saved.
     * @throws IOException
     */
    private void write(@NonNull File file, @NonNull PlugPagTransactionResult result) throws IOException {
        FileWriter writer = null;

        if (result != null &&
                !TextUtils.isEmpty(result.getDate()) &&
                !TextUtils.isEmpty(result.getTime()) &&
                !TextUtils.isEmpty(result.getCardBrand()) &&
                !TextUtils.isEmpty(result.getTransactionId()) &&
                !TextUtils.isEmpty(result.getTransactionCode()) &&
                !TextUtils.isEmpty(result.getAmount())) {
            writer = new FileWriter(file, true);
            writer.write(String.format("%s|%s|%s|%s|%s|%s\n",
                    result.getDate(),
                    result.getTime(),
                    result.getCardBrand(),
                    result.getTransactionId(),
                    result.getTransactionCode(),
                    result.getAmount()));
            writer.flush();
            writer.close();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Receipt deletion
    // ---------------------------------------------------------------------------------------------

    /**
     * Deletes a transaction from the file of its terminal/pinpad.
     *
     * @param voidData Data of the transaction to be deleted.
     * @return If the result has been deleted.
     */
    public boolean delete(@NonNull PlugPagVoidData voidData) {
        boolean deleted = false;
        String path = null;
        File file = null;
        List<String> keepLines = null;

        path = this.getFilePath(Bluetooth.getSelectedBluetoothDevice().getName());
        file = new File(path);

        try {
            if (file.exists()) {
                keepLines = this.gatherLinesToKeep(voidData, file);
                this.writeLines(keepLines, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deleted;
    }

    /**
     * Writes lines to a destination file.
     *
     * @param lines Lines to be written.
     * @param file  File to be written.
     * @throws IOException
     */
    private void writeLines(@NonNull List<String> lines, @NonNull File file) throws IOException {
        BufferedWriter writer = null;

        if (file.exists()) {
            file.delete();
        }

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        writer = new BufferedWriter(new FileWriter(file));

        for (String line : lines) {
            writer.write(line);
            writer.write('\n');
        }

        writer.flush();
        writer.close();
    }

    /**
     * Gathers lines to be kept on the transactions file.
     *
     * @param voidData Data used to validate each line.
     * @param file     File with the lines to be checked.
     * @return List of lines to be kept in the transactions file.
     * @throws Exception
     */
    private List<String> gatherLinesToKeep(@NonNull PlugPagVoidData voidData,
                                           @NonNull File file) throws Exception {
        List<String> lines = null;
        BufferedReader reader = null;
        String line = null;

        lines = new ArrayList<>();
        reader = new BufferedReader(new FileReader(file));

        while ((line = reader.readLine()) != null) {
            if (this.checkKeepLine(line, voidData)) {
                lines.add(line);
            }
        }

        reader.close();

        return lines;
    }

    /**
     * Checks if a line must be kept in the transactions file.
     *
     * @param line     Line to be checked.
     * @param voidData Data used to compared with line data.
     * @return If the line must be kept in the transactions file.
     */
    private boolean checkKeepLine(@NonNull String line, @NonNull PlugPagVoidData voidData) {
        boolean keep = false;
        String[] tokens = null;

        tokens = line.split("\\|");
        keep = tokens[3].equals(voidData.getTransactionId()) && tokens[4].equals(voidData.getTransactionCode());

        return !keep;
    }

    // ---------------------------------------------------------------------------------------------
    // Fetch receipt
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the list of transactions for a terminal/pinpad with the given serial number.
     *
     * @param serial Serial number used to search for the transactions' data
     * @return List of transactions' data.
     */
    public List<PlugPagTransactionResult> list(@NonNull String serial) {
        List<PlugPagTransactionResult> list = null;
        File file = null;
        String filePath = null;

        filePath = this.getFilePath(Bluetooth.getSelectedBluetoothDevice().getName());
        file = new File(filePath);

        try {
            list = this.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            list = new ArrayList<>();
        }

        return list;
    }

    /**
     * Reads all transaction results stored on a give file.
     *
     * @param file File where tne transaction results are stored.
     * @return List of transaction results read.
     * @throws IOException
     */
    private List<PlugPagTransactionResult> read(@NonNull File file) throws IOException {
        List<PlugPagTransactionResult> list = null;
        PlugPagTransactionResult.Builder resultBuilder = null;
        BufferedReader reader = null;
        String line = null;
        String[] tokens = null;
        SimpleDateFormat dateParser = null;
        SimpleDateFormat dateFormatter = null;

        try {
            dateParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            list = new ArrayList<>();
            reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                try {
                    tokens = line.split("\\|");
                    resultBuilder = new PlugPagTransactionResult.Builder()
                            .setDate(dateFormatter.format(dateParser.parse(tokens[0] + " " + tokens[1])))
                            .setCardBrand(tokens[2])
                            .setTransactionId(tokens[3])
                            .setTransactionCode(tokens[4])
                            .setAmount(tokens[5]);
                    list.add(resultBuilder.build());
                } catch (Exception e) {
                    // Do nothing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList<>();
        }

        return list;
    }

    // ---------------------------------------------------------------------------------------------
    // Path handling
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the path of the file where the data must be stored.
     *
     * @param serialNumber Serial number of the device that generated the receipt.
     * @return Path of the file where the receipts of the given device name is stored.
     */
    private String getFilePath(@NonNull String serialNumber) {
        String path = null;
        String basePath = null;
        String fileName = null;
        File file = null;

        basePath = this.getBasePath();
        fileName = this.getFileName(serialNumber);
        file = new File(basePath, fileName);
        path = file.getAbsolutePath();

        return path;
    }

    /**
     * Returns the base path where the receipts are saved.
     *
     * @return Base path where the receipts are saved.
     */
    private String getBasePath() {
        String path = null;

        if (this.mContext != null) {
            path = this.mContext.getFilesDir().getAbsolutePath();
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        if (path != null) {
            path = new File(path, ReceiptManager.DIRECTORY).getAbsolutePath();
        }

        return path;
    }

    /**
     * Returns the name of the file where the receipts are saved.
     *
     * @param serialNumber Serial number of the device which generated the receipts.
     * @return Name of the file used to store/read receipt information.
     */
    private String getFileName(@NonNull String serialNumber) {
        String name = null;
        String date = null;

        date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        name = String.format("%s_%s.txt", date, serialNumber);

        return name;
    }

}
