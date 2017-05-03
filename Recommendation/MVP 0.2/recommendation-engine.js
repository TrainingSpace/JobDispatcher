/* *** Author: Tharani Sivanandam @Feb 22, 2017 *** */
$(document).ready(function(){
	
	/******* Hide all the upload buttons on load *******/
	
	$('#ReqsContainer').hide();
	$('#scriptsContainer').hide();
	$('#execContainer').hide();
	$('#defectsContainer').hide();
	$('#exportScriptsIdData').hide();
	//removed to show datepicker $('#answersContainer').hide();
	$('#finalanswers').hide();
	$('#calculate').hide();	
	
	var result = '';
	
	//Get System Current Date
	var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];

	var currentDate = new Date();		
	var currentMonth; //= 3 //TEST for req q1 alert
	var currentYear; //= currentDate.getFullYear();

	//Datepicker function
	$( function() {
        $("#datepicker").datepicker({
        		   dateFormat: 'mm-dd-yy',
        		   inline: true,
                   onSelect: function() {
                      var cutDate = $(this).datepicker( 'getDate' );
                      var cutDay = cutDate.getDate();
                      var cutDateMonth = cutDate.getMonth() + 1;
                      var cutDateYear = cutDate.getFullYear();
                      alert(cutDateMonth + '-' + cutDay + '-' + cutDateYear);
                      currentMonth = cutDateMonth;
                      currentYear = cutDateYear;
                   }
         });
    });
	
	var orgReqFile = []; //original requirement csv file
	var orgScriptsCsvFile = []; //original scripts csv file
	var orgExecutionCsvFile = []; //original execution csv file
	
	/* Question 1 variables - New Features working */
	var q1CurrentMonthReqIds = []; //ReqIds from the reqs csv		
	var q1CurrentMonthScriptIds = '';  //used for final csv export		
	var finalScriptHeadr = '';
	
	/* Question 2 Variables - Did I break anything */	
	var q2PastMonthReqIds = []; //ReqIds from the reqs csv		
	var finalExecIds = []; //scriptIds from theexecution csv with status passed	
	var q2PastMonthScriptIds = '';  //used for final csv export	
	var q2JobListScriptIds = '';
	var q2JobListHeader = '';
	
	/* Question 3 Variables - How stable are top priority defects */		
	var orgDefectsCsvFile = []; //original execution csv file
	
	
	/* Results Container */
	var finalPassedIds = []; //Final scriptIds from the job results csv with status passed	
	var finalFailedIds = []; //Final scriptIds from the job results csv with status failed	
	
	var questionsVal = '';
	
	var application_name = '';
	
	$('#application').change(function() {
		//application_name = $(this).val();
		application_name = $("#application option:selected").text();
		//alert('application selected === ' + $("#application option:selected").text() );
	});	
	
	$('#questions').change(function() {
		
		//alert('The option with value ' + $(this).val() + ' was selected.');
		questionsVal = $(this).val();
		$('#threshold').val("");
		$('#reqfiles').val(''); 
		$('#scriptfiles').val(''); 
		$('#executionfiles').val('');
		$('#resultContainer').empty();
		$('#ReqsContainer').hide();
		$('#scriptsContainer').hide();
		$('#execContainer').hide();
		$('#defectsContainer').hide();
		$('#exportScriptsIdData').hide();
		//Dont Hide Cut Date field $('#answersContainer').hide();
		//alert('question selected === ' + $("#questions option:selected").text() );
		q1CurrentMonthReqIds = [];
		q1CurrentMonthScriptIds = '';
		
		q2PastMonthReqIds = [];
		q2JobListScriptIds = '';
		finalExecIds = [];
		
		if (questionsVal == 'q1') {				
			$('#ReqsContainer').show();
			$('#scriptsContainer').show();
			$('#exportScriptsIdData').show();				
			
		} else if (questionsVal == 'q2') {
			result = '';
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
	
		
	//Event Listener to select requirements
	document.getElementById('reqfiles').addEventListener('change', selectReqs, false);
			
	function selectReqs(evt) {		
		var orgReqFiles = evt.target.files; // FileList object
		orgReqFile = orgReqFiles[0];	

		if (questionsVal == 'q1') {	
			getQ1CurrentMonthReqsId(orgReqFile);
		} else if (questionsVal == 'q2') {
			getQ2PastMonthsReqsId(orgReqFile);
		}
	}		
	
	//Event Listener to select scripts
	document.getElementById('scriptfiles').addEventListener('change', selectScripts, false);
	
	function selectScripts(evt) {
		var orgScriptsCsvFiles = evt.target.files; // FileList object
		orgScriptsCsvFile = orgScriptsCsvFiles[0];
		
		if (questionsVal == 'q1') {	
			getQ1NewFeaturesJobIds(orgScriptsCsvFile);
		} else if (questionsVal == 'q2') {
			getQ2ScriptIds(orgScriptsCsvFile);			
		}

	}
	
	//Event Listener to select execution list
	document.getElementById('executionfiles').addEventListener('change', selectExecutionScripts, false);
	
	function selectExecutionScripts(evt) {
		var orgExecutionCsvFiles = evt.target.files; // FileList object
		orgExecutionCsvFile = orgExecutionCsvFiles[0];
		
		if (questionsVal == 'q2') {
			getQ2ExecutionIds(orgExecutionCsvFile);				
		}

	}
	
	//Event Listener to select defect list
	document.getElementById('defectfiles').addEventListener('change', selectDefectScripts, false);
	
	function selectDefectScripts(evt) {
		var orgDefectCsvFiles = evt.target.files; // FileList object
		orgDefectsCsvFile = orgDefectCsvFiles[0];

	}
	
	//Get the list of new requirement Ids based on current month
	function getQ1CurrentMonthReqsId(orgReqFile) {
		
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
				reqsCreationMonth = reqsCreationDate.getMonth() + 1; //changed month to a number to be compatible with datepicker
				reqsCreationYear = reqsCreationDate.getFullYear();
				
				if (reqsCreationYear == currentYear) {
					if (reqsCreationMonth == currentMonth) {
						q1CurrentMonthReqIds[j]	= orgReqsdata[0];
						j++;
					}					
				}
								
			}
		
			alert('Req Ids with current month =' + q1CurrentMonthReqIds);
		
		}
		
		reader.onerror = function(){ alert('Unable to read ' + orgReqFile.fileName); };
					
	}
	
	//Get the consolidated list of script IDs based on current month and new requirements
	function getQ1NewFeaturesJobIds(orgScriptsCsvFile) {
		var reader = new FileReader();
		reader.readAsText(orgScriptsCsvFile);
		reader.onload = function(event){
			var orgScriptCsv = event.target.result;
			var orgScriptsdata = $.csv.toArrays(orgScriptCsv);			
			var html = '';
			
			//alert(q1CurrentMonthReqIds);
						
			//output the column data
			
			var rows = orgScriptCsv.split("\n");
			//alert('rows==' + rows);
								
			var headers = rows[0].split(',');
			
			
			//var q1CurrentMonthScriptIds = '';
			
			finalScriptHeadr = headers[0];
			//alert(finalScriptHeadr);		
					
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
				
				if (testType.toLowerCase() == "auto") {
					//alert(testType.toLowerCase());
					for(var j=0; j<q1CurrentMonthReqIds.length; j++){
						if(orgScriptsdata[3] == q1CurrentMonthReqIds[j]){
							flag = true;
							//alert('reqid flag =' + orgScriptsdata[0] + q1CurrentMonthReqIds[j] + flag);							
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
						if (q1CurrentMonthScriptIds == '') {
							q1CurrentMonthScriptIds += orgScriptsdata[0];
						} else {
							q1CurrentMonthScriptIds += '\n' + orgScriptsdata[0] ;
						}
					}
				}		
								
			}
			
			q1CurrentMonthScriptIds = finalScriptHeadr + '\n' + q1CurrentMonthScriptIds;
		
			alert('Final Script Ids === ' + '\n' + q1CurrentMonthScriptIds);	
		
		};
	  reader.onerror = function(){ alert('Unable to read ' + orgScriptsCsvFile.fileName); };
			  
	}
	
	function getQ2PastMonthsReqsId(orgReqFile) {
		
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
					if (reqsCreationMonth != currentMonth) {
						q2PastMonthReqIds[j]	= orgReqsdata[0];
						j++;
					}					
				}
								
			}
		
			alert('Req Ids with past month ===' + q2PastMonthReqIds);
		
		}
		
		reader.onerror = function(){ alert('Unable to read ' + orgReqFile.fileName); };
					
	}
	
	function getQ2ScriptIds(orgScriptsCsvFile) {
		var reader = new FileReader();
		reader.readAsText(orgScriptsCsvFile);
		reader.onload = function(event){
			var orgScriptCsv = event.target.result;
			var orgScriptsdata = $.csv.toArrays(orgScriptCsv);			
			var html = '';
			
			//alert(q2PastMonthReqIds);
						
			//output the column data
			
			var rows = orgScriptCsv.split("\n");
			var headers = rows[0].split(',');
			
			
			//var q2PastMonthScriptIds = '';
			
			finalScriptHeadr = headers[0];
					
					
			//Output the column values
			for (var i=1; i<rows.length; i++) {
				
				var flag = false;
				
				var orgScriptsdata = rows[i].split(',');
				
				var testType = testType = orgScriptsdata[1];
								
				var creationDate = new Date(orgScriptsdata[4]);
				locale = "en-us",
				creationMonth = creationDate.toLocaleString(locale, { month: "long" });	
				
				creationYear = creationDate.getFullYear();
				
				
				if (testType.toLowerCase() == "auto") {
					for(var j=0; j<q2PastMonthReqIds.length; j++){
						if(orgScriptsdata[3] == q2PastMonthReqIds[j]){
							flag = true;
							//alert('reqid flag =' + orgScriptsdata[0] + q2PastMonthReqIds[j] + flag);							
						}						
					}	
					if (flag == false){
						if (creationYear == currentYear) {
							if (creationMonth != currentMonth) {
								flag = true;
							}
						}
					}
					if (flag == true) {
						if (q2PastMonthScriptIds == '') {
							q2PastMonthScriptIds += orgScriptsdata[0];
						} else {
							q2PastMonthScriptIds += '\n' + orgScriptsdata[0] ;
						}
					}
				}		
								
			}
			
			q2PastMonthScriptIds = finalScriptHeadr + '\n' + q2PastMonthScriptIds;
		
			alert('Final Q1 Script Ids less than current month === ' + '\n' + q2PastMonthScriptIds);	
		
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
			//alert('Final Script Ids === ' + '\n' + q2PastMonthScriptIds);
			
			var scriptIdArray = q2PastMonthScriptIds.split('\n');
			q2JobListHeader = scriptIdArray[0];
			
			for(var i=1; i<scriptIdArray.length; i++){
				for (var j=0; j<finalExecIds.length; j++) {
					if(scriptIdArray[i] == finalExecIds[j]){
						if (scriptIdArray == '') {
							q2JobListScriptIds = scriptIdArray[i];
						} else {
							q2JobListScriptIds += '\n' +  scriptIdArray[i];
						}
						//q2JobListScriptIds = scriptIdArray[i];
						//alert('success ===' + q2JobListScriptIds);												
					}	
				}
			}	
			
			q2JobListScriptIds = q2JobListHeader + q2JobListScriptIds;				
			alert('Q2 Script Ids with status passed === ' + '\n' + q2JobListScriptIds);	
			
		}						
	}
	

	$(".downloadJobListData").on('click', function(event) {
		if (questionsVal == ''){
			alert('Please select an option from the questions dropdown');
			
		} else if (questionsVal == 'q1') {	
			var args = [q1CurrentMonthScriptIds, 'JobIDs.csv'];			
			exportScriptIdsToCSV.apply(this, args);
			$('#answersContainer').show();
			$('#finalanswers').show();	
			$('#calculate').show();	
			
		} else if (questionsVal == 'q2') {				
			var args = [q2JobListScriptIds, 'JobIDs.csv'];			
			exportScriptIdsToCSV.apply(this, args);
			$('#answersContainer').show();
			$('#finalanswers').show();	
			$('#calculate').show();	
			
		} else if (questionsVal == 'q3') {				
			var args = [finalScriptIds, 'JobIDs.csv'];			
			exportScriptIdsToCSV.apply(this, args);
			$('#answersContainer').show();
			$('#finalanswers').show();	
			$('#calculate').show();	
		} 		
			
	});
	
	$(".answers").on('click', function(event) {
		if (questionsVal == 'q1') {	
			var rows = finalScriptIds.split("\n");
			var scriptCount = rows.length - 1;
			//alert('No: of scripts executed = ' + scriptCount);	
			
			result = 'Total No: of script executed = 5 <br>';
			result += '% passed = 60% <br>';
			result += '% failed = 40% <br>';
			result += 'Recommendation: YES';
			
			$( "#resultContainer" ).append( result );
			$('#resultContainer').show();
		} else if (questionsVal == 'q2') {				
			var rows = finalScriptIds.split("\n");
			var scriptCount = rows.length - 1;
			//alert('No: of scripts executed = ' + scriptCount);	
			
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
		
		// Deliberate 'false', see comment below
		if (false && window.navigator.msSaveBlob) {

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
		}
	}
	
	
	$('#finalanswers').on('click', function(event) {	
		
		alert('application_name==' + application_name);
		//var fileurl = '/CSVs/' + application_name + '/execution_result.csv';
		//fileurl = "'"+fileurl+"'";
		//alert('fileurl==' + fileurl);
		//$.get(fileurl, function(data) {
		
		$.get('/CSVs/SAP/execution_result.csv', function(data) {
			
			//alert(data);
			
			var rows = data.split("\n");
			var headers = rows[0].split(',');
			
			var j=0;
			var k=0;					
			
			//Output the column values
			for (var i=0; i<rows.length; i++) {
				
				var orgResultsdata = rows[i].split(',');					
				var execStatus = orgResultsdata[1].toLowerCase();
				
				//alert(execStatus);
				
				if (execStatus.trim() == "passed") {
					finalPassedIds[j]	= orgResultsdata[0];
					j++;
				} 
				
				if (execStatus.trim() == "failed") {
					finalFailedIds[k]	= orgResultsdata[0];
					k++;
				}
			}
			
			var totalScripts = rows.length - 1;
			//alert("Total no: of scripts executed ==" + totalScripts);
			
			//alert('Execution Ids with status passed =' + finalPassedIds);
			//alert('Total script Ids with status passed =' + finalPassedIds.length);
			
			var percentagePassed = (finalPassedIds.length/totalScripts)*100;
			//alert('Percentage passed =' + percentagePassed);
			
			//alert('Execution Ids with status failed =' + finalFailedIds);
			//alert('Total script Ids with status failed =' + finalFailedIds.length);
			
			var percentageFailed = (finalFailedIds.length/totalScripts)*100;
			//alert('Percentage failed =' + percentageFailed);
			
			var thereshold = $("#threshold").val(); 
			//alert('thereshold entered==' + thereshold);
			
			var recommendation = '';
			
			if (percentagePassed >= thereshold) {
				//alert ('recommendation is YES');
				recommendation = "YES";
			} else {
				//alert ('recommendation is NO');
				recommendation = "NO";
			}
			
			var result = 'Total No: of scripts executed = ' + totalScripts + '<br>';
			result += '% passed = ' + percentagePassed + '<br>';
			result += '% failed = ' + percentageFailed + '<br>';
			result += 'Recommendation: ' + recommendation;
			
			$( "#resultContainer" ).append( result );
			$('#resultContainer').show();
			
		});
	});
	
});