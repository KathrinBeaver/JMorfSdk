# JMorfSdk<br>
<b>JMorfSdk</b> - главный класс, содержит <b>HashMap</b> словоформ (<b>OmoForm</b>). <br>
<br>
<b>Form</b> - форма слова содержит в себе набор характеристик, имеет набор характеристик, которые меняются в зависимости от словоформы (число, падеж и т.д.) <br>
<b>WordForm</b> - словоформа, наследуется от <b>Form</b>, имеет ссылку на <b>InitialForm</b>. <br>
<b>InitialForm</b> наследуется от <b>Form</b> - словоформа в начальной форме слова, имеет ссылки на все производные словоформы (<b>WordfForm</b>). <br>
<b>MorphologyParameters</b> - набор констант для морфологических характеристик. <br>
Пример работы с библиотекой описан в <b>Running</b> <br>
<b>ВНИМАНИЕ РЕАЛИЗОВАН ФУНКЦИОНАЛ В РЕЖИМЕ АНАЛИЗА И ЧАСТИЧНО В РЕЖИМЕ ГЕНЕРАЦИИ! </b>
<br>
v 2.10 от 30.12.2017 <br>
1) Символьное представление словоформы теперь храниться в базе данных (для начальной формы в <b>dictionary.initialFormString.bd</b>, для производной формы в <b>dictionary.wordFormString.bd</b>, что уменьшает издержки в ОП. <br>
2) База данных символьного представления и морфологические параметры хранятся в отдельные сборки MorphologicalStructures, что позволяет обрабатывать промежуточные результаты без JMorfSdk. <br>
3) Добавлен режим генерации словоформе по начальной форме и наборы морф. характеристик. <br>
4) JMorfSdk стала потокобезопасной. Теперь получать морф. характеристики можно одновременно с нескольких потоков, причем потоки не мешают друг другу и не блокируют библиотеку. <br>
5) Появилась поддержка цифр, теперь число "1945" корректно распознается, как словоформа числа. <br>
6) Теперь слова через дефис распознаются как слово, например, "что-то", "кто-то". <br>
7) Все методы для работы с библиотекой возвращают Exception в случае, если слово не было найдено в словаре или случилось иная ошибка. <br>
8) LoadJMorfSdk переименован в JMorfSdkLoad. <br>
9) Если не нужна генерация слова, то можно удалить базу <b>dictionary.wordFormString.bd</b>, но тогда загружать библиотеку следует с помощью <b>JMorfSdkLoad.loadInAnalysisMode</b> <br>

