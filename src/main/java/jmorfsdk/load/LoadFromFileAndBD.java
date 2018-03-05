/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */
package jmorfsdk.load;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import jmorfsdk.JMorfSdk;
import jmorfsdk.form.InitialForm;
import jmorfsdk.form.WordForm;
import template.wrapper.classes.FileHelper;

import static load.BDFormString.deCompressDd;

public final class LoadFromFileAndBD {

    static {
        deCompressDd();
    }

    private LoadFromFileAndBD() {}

    protected static JMorfSdk loadInAnalysisMode(String pathZipFiel, boolean isLoadGenerationMode) {
        ZipInputStream streamHashAndMorfCharacteristic = null;
        try {
            streamHashAndMorfCharacteristic = FileHelper.openZipFile(pathZipFiel, Property.PATH_HASH_AND_MORF_CHARACTERISTICS);
            return loadJMorfSdk(streamHashAndMorfCharacteristic, isLoadGenerationMode);
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
            return JMorfSdk.getEmptyJMorfSdk();
        } finally {
            FileHelper.closeFile(streamHashAndMorfCharacteristic);
        }
    }

    private static JMorfSdk loadJMorfSdk(InputStream inputStreamHashAndMorfCharacteristics, boolean isLoadFormInInitialForm) {

        JMorfSdk jMorfSdk = JMorfSdk.getEmptyJMorfSdk();
        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamHashAndMorfCharacteristics)) {
            while (inputStream.available() > 0) {
                int hashCode = getIntFromBytes(inputStream);
                InitialForm initialForm = createInitialForm(inputStream);
                jMorfSdk.addForm(hashCode, initialForm);
                addWordForm(jMorfSdk, initialForm, inputStream, isLoadFormInInitialForm);
            }
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jMorfSdk;
    }

    private static InitialForm createInitialForm(BufferedInputStream inputStream) throws IOException {
        return new InitialForm(
                getIntFromBytes(inputStream),
                getTypeOfSpeechFromBytes(inputStream),
                getMorfCharacteristicsFromBytes(inputStream));
    }

    private static void addWordForm(JMorfSdk jMorfSdk, InitialForm initialForm, InputStream inputStream, boolean isLoadFormInInitialForm) {
        int nextHashCode = getIntFromBytes(inputStream);
        while (nextHashCode != Property.CONTROL_VALUE) {
            WordForm wordForm = new WordForm(getIdForm(inputStream), getMorfCharacteristicsFromBytes(inputStream), initialForm);
            jMorfSdk.addForm(nextHashCode, wordForm);
            if (isLoadFormInInitialForm) {
                initialForm.addWordfFormInList(wordForm);
            }
            nextHashCode = getIntFromBytes(inputStream);
        }
        initialForm.trimToSize();
    }

    private static int getIdForm(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
    }

    private static int getIntFromBytes(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
    }

    private static byte getTypeOfSpeechFromBytes(InputStream inputStream) throws IOException {
        byte typeOfSpeech = (byte) inputStream.read();
        return typeOfSpeech;
    }

    private static long getMorfCharacteristicsFromBytes(InputStream inputStream) {
        return getValueCodeFromBytes(inputStream, 8);
    }

    private static long getValueCodeFromBytes(InputStream inputStream, int countByte) {
        int hashCode = 0;
        try {
            for (int i = 0; i < countByte; i++) {
                int f = 0xFF & inputStream.read();
                int g1 = f << (8 * (countByte - 1 - i));
                hashCode |= g1;
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadFromFileAndBD.class.getName())
                .log(Level.SEVERE, String.format("Не ожиданное окончание файла, проверте целостность файлов!%s",
                        Property.MOVE_TO_NEW_LINE), ex);
        }

        return hashCode;
    }

}
