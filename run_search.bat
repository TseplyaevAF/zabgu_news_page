rem Первый аргумент - тип программы (parse/search)
rem Второй аргумент - файл, в текстах которого будут искаться совпадения
rem Третий аргумент - слово для поиска

java -cp "bin;lib\*;lib\opencsv\*" App search data.csv "хакатон"
pause