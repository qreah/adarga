Select companyName, symbol,
	format(cast(SUM(CASE WHEN finantialDate = '2019' THEN value/1000000 ELSE NULL END) as char), 0) AS A2019,
	format(cast(SUM(CASE WHEN finantialDate = '2018' THEN value/1000000 ELSE NULL END) as char),0) AS A2018,
	format(cast(SUM(CASE WHEN finantialDate = '2017' THEN value/1000000 ELSE NULL END) as char) ,0) AS A2017,
	format(cast(SUM(CASE WHEN finantialDate = '2016' THEN value/1000000 ELSE NULL END) as char) ,0) AS A2016,
    SUM(CASE WHEN finantialDate = '2018' THEN value ELSE NULL END) as ordenar
from (
	SELECT companyName, symbol, value, finantialDate FROM apiadbossDB.adargaConcepts
	where concept = 'Revenue') revenue
    
group by 1, 2
order by ordenar desc
	