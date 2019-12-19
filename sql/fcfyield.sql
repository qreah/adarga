SELECT companyName, symbol, value, reportDate, industry, description FROM apiadbossDB.adargaConcepts
	where concept = 'FCFY'
		and value > 0.08
        and value is not NULL
        and value < 0.50
        and industry != ''
	order by 3 desc
        