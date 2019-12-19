SELECT revenue.symbol, 
	revenue.revenue, 
    operatingincome.operatingincome,
    revenue.revenue / operatingincome.operatingincome

FROM 

(select symbol, 
	truncate(value, 0) as revenue, 
    finantialDate
from apiadbossDB.adargaConcepts
where concept = 'Revenue'
order by 1, 2 desc) as revenue

INNER JOIN  

(select symbol, 
	truncate(value, 0) as operatingincome, 
    finantialDate
from apiadbossDB.adargaConcepts
where concept = 'OperatingIncome'
order by 1, 2 desc) as operatingincome

ON revenue.symbol = operatingincome.symbol and
	revenue.finantialDate = operatingincome.finantialDate