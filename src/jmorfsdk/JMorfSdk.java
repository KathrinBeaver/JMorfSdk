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
 * Если нет, см. <Https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 */
package jmorfsdk;

import jmorfsdk.form.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public final class JMorfSdk {

    //По этому мапу находим морфологические характеристики слова
    private HashMap<Integer, OmoForms> omoForms = new HashMap();
    //По этому мапу находим словоформу с заданными характеристиками
    private HashMap<Integer, MainForm> mainForms = new HashMap();

    private void addOmoForm(OmoForms of) {
        omoForms.put(of.hashCode(), of);
    }

    public void addMainForm(MainForm mf) {
        mainForms.put(mf.hashCode(), mf);
    }

    /**
     * Проверяем существование омоформы для входной формы, если нашли, то
     * добавляем ее туда, иначе создаем новую
     *
     * @param form
     */
    public void addFormInOmoForm(Form form) {
        if (isOmoFormExistForForm(form)) {
            getOmoFormByForm(form).addForm(form);
        } else {
            addOmoForm(new OmoForms(form));
        }
    }

    /**
     * Проверка существования словоформы
     *
     * @param form
     * @return если найде, то возвращаем true, иначе false
     */
    public boolean isOmoFormExistForForm(Form form) {
        return omoForms.containsKey(form.hashCode());
    }

    public OmoForms getOmoFormByForm(Form form) {
        return omoForms.get(form.hashCode());
    }

    public void start(String pathLibrary, String encoding) {
        System.out.println("Старт загрузки библиотеки");
        BufferedReader buffInput = openStreamFromFile(pathLibrary, encoding);
        loadFromFile(buffInput);
        closeStream(buffInput);
        System.out.println("Библиотека готова к работе.");
    }

    private BufferedReader openStreamFromFile(String pathLibrary, String encoding) {
        BufferedReader buffInput = null;
        try {
            buffInput = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), encoding));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }

        return buffInput;
    }

    private void closeStream(BufferedReader buffInput) {
        try {
            buffInput.close();
        } catch (IOException ex) {
            Logger.getLogger(JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadFromFile(BufferedReader buffInput) {
        try {
            buffInput.readLine();
            int count = 0;
            while (buffInput.ready() && count < 50000) {
                addLemma(buffInput.readLine());
                //count++;
            }
        } catch (IOException ex) {
            Logger.getLogger("Ошибка при чтении файла " + JMorfSdk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addLemma(String strLemma) {
        MainForm mainForm = createMainForm(strLemma);
        addMainForm(mainForm);
        addWordForms(strLemma, mainForm);
    }

    private MainForm createMainForm(String strForms) {
        String mainWordForm;
        if(strForms.matches("\"")) {
            mainWordForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            mainWordForm = strForms;
        }
        String[] mainWordParameters = mainWordForm.split(" ");
        return new MainForm(mainWordParameters[0], mainWordParameters[1], mainWordParameters[2]);
    }

    private void addWordForms(String strLemma, MainForm mainForm) {

        String[] arrayWordForms = strLemma.split("\"");

        for (int i = 1; i < arrayWordForms.length; i++) {
            WordForm wordForm = createWordForm(arrayWordForms[i], mainForm);
            addFormInOmoForm(wordForm);
        }
    }

    private WordForm createWordForm(String strForm, MainForm mainForm) {
        String[] mainWordParam = strForm.split(" ");
        return new WordForm(mainWordParam[0], mainWordParam[1], mainForm);
    }

    public void finish() {
        omoForms = null;
        mainForms = null;
    }
}