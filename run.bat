chcp 1251
rem Первый аргумент - количество страниц
rem Второй аргумент - название файла для сохранения данных
rem Аргументы прописываются после слова App

java -cp "bin;lib\*;lib\opencsv\*" App 2 data2.csv previews2
pause