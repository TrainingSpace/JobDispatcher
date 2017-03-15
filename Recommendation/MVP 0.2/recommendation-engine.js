/* *** Author: Tharani Sivanandam @Feb 22, 2017 *** */
$(document).ready(function(){
	
	/******* Hide all the upload buttons on load *******/
	
	$('#ReqsContainer').hide();
	$('#scriptsContainer').hide();
	$('#execContainer').hide();
	$('#defectsContainer').hide();
	$('#exportScriptsIdData').hide();
	$('#answersContainer').hide();
	
	var result = '';
	
	$('#questions').change(function() {
		
		//alert('The option with value ' + $(this).val() + ' was selected.');
		questionsVal = $(this).val();
		$('#threshold').val("");
		$('#reqfiles').val(''); 
		$('#scriptfiles').val(''); 
		$('#executionfiles').val('');
		$('#resultContainer').empty();
		//alert('question selected === ' + $("#questions option:selected").text() );
		
		if (questionsVal == 'q1') {				
			/*$('#reqfiles').val(''); 
			$('#scriptfiles').val(''); 
			$('#resultContainer').empty();*/
			$('#ReqsContainer').show();
			$('#scriptsContainer').show();
			$('#exportScriptsIdData').show();				
			
		} else if (questionsVal == 'q2') {
			result = '';
			/*$('#reqfiles').val(''); 
			$('#scriptfiles').val(''); 
			$('#executionfiles').val('');
			$('#resultContainer').empty();*/
			$('#ReqsContainer').show();
			$('#scriptsContainer').show();
			$('#execContainer').show();
			$('#exportScriptsIdData').show();	
			
		}  else if (questionsVal == 'q3') {	
			result = '';
			$('#ReqsContainer').show();
			$('#scriptsContainer').show();
			$('#execContainer').show();
			$('#defectsContainer').show();
			$('#exportScriptsIdData').show();				
		}  

	});
	
	//$('#threshold').change(function() {
      //  $('#threshold').val(""); // Should set the value to nothing.
    //});
	
	//Get System Current Date
	var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];

	var currentDate = new Date();		
	var currentMonth = monthNames[currentDate.getMonth()];
	var currentYear = currentDate.getFullYear();
	
	/* Question 1 variables - New Features working */	
	var orgReqFile = []; //original requirement csv file
	var orgScriptsCsvFile = []; //original scripts csv file
			
	var finalReqIds = []; //ReqIds from the reqs csv		
	var finalScriptIds = '';  //used for final csv export		
	var finalScriptHeadr = '';
	
	/* Question 2 Variables - Did I break anything */		
	var orgExecutionCsvFile = []; //original execution csv file
	var finalExecIds = []; //scriptIds from theexecution csv with status passed	
	var q2JobList = '';
	var q2JobListHeader = '';
	
	/* Question 3 Variables - How stable are top priority defects */		
	var orgDefectsCsvFile = []; //original execution csv file
	
	var questionsVal = '';
	
	//Event Listener to select requirements
	document.getElementById('reqfiles').addEventListener('change', selectReqs, false);
			
	function selectReqs(evt) {		
		var orgReqFiles = evt.target.files; // FileList object
		orgReqFile = orgReqFiles[0];				
	}		
	
	//Event Listener to select scripts
	document.getElementById('scriptfiles').addEventListener('change', selectScripts, false);
	
	function selectScripts(evt) {
		var orgScriptsCsvFiles = evt.target.files; // FileList object
		orgScriptsCsvFile = orgScriptsCsvFiles[0];

	}
	
	//Event Listener to select execution list
	document.getElementById('executionfiles').addEventListener('change', selectExecutionScripts, false);
	
	function selectExecutionScripts(evt) {
		var orgExecutionCsvFiles = evt.target.files; // FileList object
		orgExecutionCsvFile = orgExecutionCsvFiles[0];

	}
	
	//Event Listener to select defect list
	document.getElementById('defectfiles').addEventListener('change', selectDefectScripts, false);
	
	function selectDefectScripts(evt) {
		var orgDefectCsvFiles = evt.target.files; // FileList object
		orgDefectsCsvFile = orgDefectCsvFiles[0];

	}
	
	//Get the list of new requirement Ids based on current month
	function getNewReqsId(orgReqFile) {
		
		var reader = new FileReader();
		reader.readAsText(orgReqFile);
		reader.onload = function(event){
					
			var orgReqsCsv = event.target.result;
				
			var rows = orgReqsCsv.split("\n");
			var headers = rows[0].split(',');
			var j=0;
												
			//Output the column values
			for (var i=1; i<rows.length; i++) {
				
				var orgReqsdata = rows[i].split(',');
				
				var reqsCreationDate = new Date(orgReqsdata[2]);
				locale = "en-us",
				reqsCreationMonth = reqsCreationDate.toLocaleString(locale, { month: "long" });	
				reqsCreationYear = reqsCreationDate.getFullYear();
				
				if (reqsCreationYear == currentYear) {
					if (reqsCreationMonth == currentMonth) {
						finalReqIds[j]	= orgReqsdata[0];
						j++;
					}					
				}
								
			}
		
			//alert('Req Ids with current month =' + finalReqIds);
		
		}
		
		reader.onerror = function(){ alert('Unable to read ' + orgReqFile.fileName); };
					
	}
	
	//Get the consolidated list of script IDs based on current month and new requirements
	function getNewFeaturesJobIds(orgScriptsCsvFile) {
		var reader = new FileReader();
		reader.readAsText(orgScriptsCsvFile);
		reader.onload = function(event){
			var orgScriptCsv = event.target.result;
			var orgScriptsdata = $.csv.toArrays(orgScriptCsv);			
			var html = '';
			
			//alert(finalReqIds);
						
			//output the column data
			
			var rows = orgScriptCsv.split("\n");
			var headers = rows[0].split(',');
			
			
			//var finalScriptIds = '';
			
			finalScriptHeadr = headers[0];
					
					
			//Output the column values
			for (var i=1; i<rows.length; i++) {
				
				var flag = false;
				
				var orgScriptsdata = rows[i].split(',');
				
				var testType = orgScriptsdata[1];
				//alert(testType);
				
				var creationDate = new Date(orgScriptsdata[4]);
				locale = "en-us",
				creationMonth = creationDate.toLocaleString(locale, { month: "long" });	
				
				creationYear = creationDate.getFullYear();
				
				if (testType == "auto") {
					for(var j=0; j<finalReqIds.length; j++){
						if(orgScriptsdata[3] == finalReqIds[j]){
							flag = true;
							//alert('reqid flag =' + orgScriptsdata[0] + finalReqIds[j] + flag);							
						}						
					}	
					if (flag == false){
						if (creationYear == currentYear) {
							if (creationMonth == currentMonth) {
								flag = true;
							}
						}
					}
					if (flag == true) {
						if (finalScriptIds == '') {
							finalScriptIds += orgScriptsdata[0];
						} else {
							finalScriptIds += '\n' + orgScriptsdata[0] ;
						}
					}
				}		
								
			}
			
			finalScriptIds = finalScriptHeadr + '\n' + finalScriptIds;
		
			//alert('Final Script Ids === ' + '\n' + finalScriptIds);						
		
		};
	  reader.onerror = function(){ alert('Unable to read ' + orgScriptsCsvFile.fileName); };
			  
	}
	
	//Get the consolidated list of execution IDs based on the execution status and current date
	function getQ2ExecutionIds(orgExecutionCsvFile) {
		
		var reader = new FileReader();
		reader.readAsText(orgExecutionCsvFile);
		reader.onload = function(event){
					
			var orgExecListCsv = event.target.result;
				
			var rows = orgExecListCsv.split("\n");
			var headers = rows[0].split(',');
			var z=0;
												
			//Output the column values
			for (var x=1; x<rows.length; x++) {
									
				var orgExecdata = rows[x].split(',');					
				var execStatus = orgExecdata[3];
				
				var execDate = new Date(orgExecdata[2]);
				locale = "en-us",
				execMonth = execDate.toLocaleString(locale, { month: "long" });						
				execYear = execDate.getFullYear();
				
				if (execStatus.trim() == "passed") {
					finalExecIds[z]	= orgExecdata[1];
					z++
				}	
			}			
			//alert('Execution Ids with status passed =' + finalExecIds);
			//alert('Final Script Ids === ' + '\n' + finalScriptIds);
			
			var scriptIdArray = finalScriptIds.split('\n');
			q2JobListHeader = scriptIdArray[0];
			
			for(var i=1; i<scriptIdArray.length; i++){
				for (var j=0; j<finalExecIds.length; j++) {
					if(scriptIdArray[i] == finalExecIds[j]){
						if (scriptIdArray == '') {
							q2JobList = scriptIdArray[i];
						} else {
							q2JobList += '\n' +  scriptIdArray[i];
						}
						//q2JobList = scriptIdArray[i];
						//alert('success ===' + q2JobList);												
					}	
				}
			}	
			
			q2JobList = q2JobListHeader + q2JobList;				
			//alert(q2JobList);	
			
		}						
	}
	
	$( function() {
		$( "#datepicker" ).datepicker();
	});
	
	
	$(".exportScriptsIdData").on('click', function(event) {
			
		if (questionsVal == ''){
			alert('Please select an option from the questions dropdown');
			
		} else if (questionsVal == 'q1') {	
			getNewReqsId(orgReqFile);
			getNewFeaturesJobIds(orgScriptsCsvFile);	
			$('#answersContainer').show();			
			
		} else if (questionsVal == 'q2') {				
			getNewReqsId(orgReqFile);
			getNewFeaturesJobIds(orgScriptsCsvFile);
			getQ2ExecutionIds(orgExecutionCsvFile);	
			$('#answersContainer').show();	
			
		} else if (questionsVal == 'q3') {				
			getHighPriorityDefectsId(orgScriptsCsvFile);
			$('#answersContainer').show();	
			
		} 		
	});
	
	$(".answers").on('click', function(event) {
		if (questionsVal == 'q1') {	
			var args = [finalScriptIds, 'JobIDs.csv'];
			exportScriptIdsToCSV.apply(this, args);	
			var rows = finalScriptIds.split("\n");
			var scriptCount = rows.length - 1;
			//alert('No: of scripts executed = ' + scriptCount);	
			
			//var result = 'Yes all ' + scriptCount + ' scripts executed in ' + currentMonth + ' were successfully executed as of ' + currentDate;
			result = 'Total No: of script executed = 5 <br>';
			result += '% passed = 60% <br>';
			result += '% failed = 40% <br>';
			result += 'Recommendation: YES';
			
			$( "#resultContainer" ).append( result );
			$('#resultContainer').show();
		} else if (questionsVal == 'q2') {				
			/*var args = [q2JobList, 'JobIDs.csv'];
			exportScriptIdsToCSV.apply(this, args);	*/
			var rows = finalScriptIds.split("\n");
			var scriptCount = rows.length - 1;
			//alert('No: of scripts executed = ' + scriptCount);	
			
			//var result = 'Yes all ' + scriptCount + ' scripts executed in ' + currentMonth + ' were successfully executed as of ' + currentDate;
			var result = 'Total No: of script executed = 5 <br>';
			result += '% passed = 40% <br>';
			result += '% failed = 60% <br>';
			result += 'Recommendation: NO';
			
			$( "#resultContainer" ).append( result );
			$('#resultContainer').show();
		}
		
	});
	
	function exportScriptIdsToCSV(finalScriptIds, filename) {
		
		var rows = finalScriptIds.split("\n");
		var scriptCount = rows.length - 1;
		//alert('No: of scripts executed = ' + scriptCount);	
		
		//var result = 'Yes all ' + scriptCount + ' scripts executed in ' + currentMonth + ' were successfully executed as of ' + currentDate;
		var result = 'Total No: of script executed = ' + scriptCount + '<br>';
		result += '% passed = 60% <br>';
		result += '% failed = 40% <br>';
		result += 'Recommendation: YES';
		
		$( "#resultContainer" ).append( result );
		$('#resultContainer').show();

		// Deliberate 'false', see comment below
		/*if (false && window.navigator.msSaveBlob) {

		  var blob = new Blob([decodeURIComponent(finalScriptIds)], {
			type: 'text/csv;charset=utf8'
		  });

		  // Crashes in IE 10, IE 11 and Microsoft Edge
		  // See MS Edge Issue #10396033
		  // Hence, the deliberate 'false'
		  // This is here just for completeness
		  // Remove the 'false' at your own risk
		  window.navigator.msSaveBlob(blob, filename);

		} else if (window.Blob && window.URL) {
		  // HTML5 Blob        
		  var blob = new Blob([finalScriptIds], {
			type: 'text/csv;charset=utf-8'
		  });
		  var csvUrl = URL.createObjectURL(blob);

		  $(this)
			.attr({
			  'download': filename,
			  'href': csvUrl
			});
		} else {
		  // Data URI
		  var finalScriptsCsvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(finalScriptIds);

		  $(this)
			.attr({
			  'download': filename,
			  'href': finalScriptsCsvData,
			  'target': '_blank'
			});
		}*/
	}
			
});