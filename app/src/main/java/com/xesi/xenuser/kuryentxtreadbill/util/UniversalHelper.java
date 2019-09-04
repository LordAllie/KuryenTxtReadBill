package com.xesi.xenuser.kuryentxtreadbill.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xesi.xenuser.kuryentxtreadbill.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Daryll Sabate on 1/4/2018.
 */

public class UniversalHelper {

    public final static int WIDTH = 384;
    public final static int HEIGHT = 340;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    public static DecimalFormat df = new DecimalFormat("#0.00");
    public static DecimalFormat dformat = new DecimalFormat("#0.0000");
    public static DecimalFormat dformatter = new DecimalFormat("#,##0.00");
    public static int WHITE = 0xFFFFFFFF;
    public static int BLACK = 0xFF000000;
    private Context context;
    private String jsonDIR;
    public UniversalHelper(Context context) {
        this.context = context;
    }

    public static String padLeftDynamic(String originalString, int lengthParam) {
        String padCharacter = " ";
        String space = "";
        int counter = 0;
        int len = originalString.length();
        if (len < lengthParam) {
            int numOfSpace = (lengthParam - len) / 2;
            while (counter < numOfSpace) {
                space = space + padCharacter;
                counter++;
            }
            originalString = space + originalString;
        }
        return originalString;
    }

    public static String padLeft(String originalString, int lengthParam) {

        String padCharacter = " ";
        String space = "";
        int length = lengthParam;
        String paddedString = originalString;
        int counter = paddedString.length();

        if (paddedString.length() < length) {
            while (counter != length) {
                space = space + padCharacter;
                counter++;
                if (counter == length) {
                    paddedString = space + paddedString;
                }
            }
        }

        return paddedString;
    }

    public static String padRight(String originalString, int lengthParam) {

        String padCharacter = " ";
        String space = "";
        int length = lengthParam;
        String paddedString = originalString;
        int counter = paddedString.length();

        if (paddedString.length() < length) {
            while (counter != length) {
                space = space + padCharacter;
                counter++;
                if (counter == length) {
                    paddedString = paddedString + space;
                }
            }
        }

        return paddedString;
    }

    public static String checkPaperStatus(int x) {
        String msg = "OK";
        switch (x) {
            case -2:
                msg = " Check printer status failed";
                break;
            case -1:
                msg = " Printer overheat";
                break;
            case 0:
                msg = " No paper ";
                break;
            case 2:
                msg = " Paper jam";
                break;
            default:
                break;
        }
        return msg;
    }

    public String setUpBaseURL(String serverIp, String port) {
        String server;
        if (port.matches(""))
            server = serverIp.trim();
        else
            server = serverIp.trim() + ":" + port.trim();
        return context.getResources().getString(R.string.http) + server
                + context.getResources().getString(R.string.base_url);
    }

    public void encodeAsBitmap(String qrCode) throws Exception {
        BitMatrix result;
        ByteBuffer buffer;
        FileOutputStream outputStream;
        String qrData;
        try {
            qrData = dataEncrypt(qrCode);
            Log.d("QRCODE", qrData);
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
            result = new MultiFormatWriter().encode(qrData,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);

            buffer = AndroidBmpUtil.save(bitmap);
            outputStream = context.openFileOutput(context.getString(R.string.qrcode_filename), Context.MODE_PRIVATE);
            outputStream.write(buffer.array());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void encodeAsBitmapBarcode(String data) throws Exception {
        BitMatrix result;
        ByteBuffer buffer;
        FileOutputStream outputStream;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
            result = new MultiFormatWriter().encode(data,
                    BarcodeFormat.CODE_128, 384, 50, hints);
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);

            buffer = AndroidBmpUtil.save(bitmap);
            outputStream = context.openFileOutput(context.getString(R.string.barcode_filename), Context.MODE_PRIVATE);
            outputStream.write(buffer.array());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double calculateResetMeterConsumption(double curRdg, double prevRdg) {
        String s = Double.toString(prevRdg);
        int dot = s.indexOf('.');
        String maxVal = "";
        /* get the max meter reading value */
        for (int x = 1; dot >= x; x++) {
            maxVal = maxVal + "9";
        }

        double dMaxVal = Double.parseDouble(maxVal) + 1;
        double initialValue = dMaxVal - prevRdg;
        double totalConsumption = initialValue + curRdg;

        return totalConsumption;
    }

    public String getBillValidityDate(String dueDate, String daysToAdd) {
        String billValidUntil = dueDate;
        Log.d("QRCODE", "VALID DATE X" + billValidUntil);
        Date date;
        Calendar cal = Calendar.getInstance();
        try {
            date = sdf.parse(dueDate);
            cal.setTime(date);
            cal.add(Calendar.DATE, Integer.parseInt(daysToAdd));
            billValidUntil = dateFormat.format(cal.getTime());
            Log.d("QRCODE", "VALID DATE Y" + billValidUntil);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return billValidUntil;
    }

    public void saveBackup(String retValue, String billNo) {
        try {
            if(externalMemoryAvailable())
                jsonDIR= "/sdcard";
            else
                jsonDIR= String.valueOf(Environment.getExternalStorageDirectory());

            File fileDir = new File(jsonDIR, "RNBFile");
            if (!fileDir.exists()) {
                fileDir.mkdir();
                Log.d("RNB", "Directory Created");
            } else
                Log.d("RNB", "Directory Found");

            File outputFile = new File(fileDir.getAbsolutePath(), billNo + ".json");
            MediaScannerConnection.scanFile(context, new String[]{outputFile.toString()}, null, null);
            if (outputFile.exists())
                outputFile.delete();
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(retValue);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {}
    }

    public static boolean externalMemoryAvailable() {
        if (Environment.isExternalStorageRemovable()) {
            //device support sd card. We need to check sd card availability.
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED) || state.equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            //device not support sd card.
            return false;
        }
    }

    public List<String> formatStringByMaxLength(String stringToFormat, int maxLength) {
        List<String> formattedStrings = new ArrayList<>();
        String formattedString = addLinebreaks(stringToFormat, maxLength);
        String[] sData = formattedString.split("\n");
        for (String s : sData) {
            formattedStrings.add(s);
        }
        return formattedStrings;
    }

    private String addLinebreaks(String toFormat, int length) {
        StringTokenizer tok = new StringTokenizer(toFormat, " ");
        StringBuilder output = new StringBuilder(toFormat.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (lineLen + word.length() > length) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");
            lineLen += word.length();
        }
        return output.toString();
    }

    public List<String> generateMonth(Date date) {
        List<String> monthList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        date = cal.getTime();
        String monthToday = sdf.format(date);
        Log.d("MONTH", String.valueOf(monthToday.charAt(0)));
        monthList.add(String.valueOf(monthToday.charAt(0)));
        try {
            for (int x = 0; x < 11; x++) {
                cal.add(Calendar.MONTH, 1);
                date = cal.getTime();
                monthToday = sdf.format(date);
                Log.d("MONTH", String.valueOf(monthToday.charAt(0)));
                monthList.add(String.valueOf(monthToday.charAt(0)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthList;
    }

    public void saveToBitmap(LinearLayout llGraph) {
        llGraph.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llGraph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Bitmap b = Bitmap.createBitmap(384, 340, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                c.drawColor(Color.WHITE);
                c.drawBitmap(b, 0, 0, null);
                llGraph.layout(llGraph.getLeft(), llGraph.getTop(), llGraph.getRight(), llGraph.getBottom());
                llGraph.draw(c);
                String filename = "graph.bmp";
                File sd = Environment.getExternalStorageDirectory();
                File dest = new File(sd, filename);

                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    b.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    ByteBuffer buffer = AndroidBmpUtil.save(b);
                    out = context.openFileOutput("chart.bmp", Context.MODE_PRIVATE);
                    out.write(buffer.array());
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("CHARTTAG", filename);
            }
        });
    }

    public static BigDecimal rounding(BigDecimal data, String rounding){
        if(rounding.toUpperCase().equals("ROUND_HALF_UP"))
            data = data.setScale(2,BigDecimal.ROUND_HALF_UP);
        else if (rounding.toUpperCase().equals("ROUND_UP"))
            data = data.setScale(2,BigDecimal.ROUND_UP);
        else if (rounding.toUpperCase().equals("ROUND_DOWN"))
            data = data.setScale(2,BigDecimal.ROUND_DOWN);
        else if (rounding.toUpperCase().equals("ROUND_HALF_DOWN"))
            data = data.setScale(2,BigDecimal.ROUND_HALF_DOWN);
        else if (rounding.toUpperCase().equals("ROUND_HALF_EVEN"))
            data = data.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        else
            data = data.setScale(2,BigDecimal.ROUND_HALF_UP);
        return data;
    }

    public String dataEncrypt(String dataToEncrypt) throws UnsupportedEncodingException {
        //   String rectifiedString = Base64S.replace("\\","");
        return Base64.encodeToString(dataToEncrypt.getBytes("UTF-8"), Base64.DEFAULT);
    }

    public void saveJsonHeader(String retValue) {
        try {
            if(externalMemoryAvailable())
                jsonDIR= "/sdcard";
            else
                jsonDIR= String.valueOf(Environment.getExternalStorageDirectory());

            File fileDir = new File(jsonDIR, "RNBFile");
            if (!fileDir.exists()) {
                fileDir.mkdir();
                Log.d("RNB", "Directory Created");
            } else
                Log.d("RNB", "Directory Found");

            File outputFile = new File(fileDir.getAbsolutePath(), "header.json");
            MediaScannerConnection.scanFile(context, new String[]{outputFile.toString()}, null, null);
            if (outputFile.exists())
                outputFile.delete();
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(retValue);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {}
    }

}
