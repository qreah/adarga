select * from apiadbossDB.adargaConcepts
where symbol = 'PDLI'
	# and finantialDate = '2018'
    and concept = 'dividendYield'
order by finantialDate asc