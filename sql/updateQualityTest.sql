UPDATE apiadbossDB.qualityTest SET reviewed = 1 
WHERE 
	actualDate = '2019-06-19 at 00:00:00 UTC'
    AND companySymbol = 'ACN'
    AND concept = 'Inventories'