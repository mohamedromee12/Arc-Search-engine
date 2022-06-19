apache folder includes the files for the Interface, query processing and phrase search.
interface folder includes the interface , ranker , query proccessing , phrase searching but you can run the apache directly
Crawler and indexer folders includes their full project.
Data folder is used for the shared data between the indexer and the crawler.

there is a read me inside each of the folder except Data

you should run the crawler first then the indexer 
and change the path of the stopwords in the NameGender class located in Arc\apache\ROOT\WEB-INF\classes
to match the current path the file is provided within the same directory