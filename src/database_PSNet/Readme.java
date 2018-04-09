/*
 * ### INITIALIZATION ###
 * 
 * 1. Run sql_PSNet/caseInfo_persInfo.sql in MYSQL workbench, to create title of the table "caseinfo" and "persinfo"
 * 
 * 2. Run CreateTableForCaseName.java, to insert contents to "caseinfo"
 * 		input: PSNet/case name.xls
 * 		output: write contents to "caseinfo"
 * 	
 * 3. Run CreateTableForPerspective.java, to insert contents to "persinfo"
 * 		input: PSNet/perspective.xls
 * 		output: write contents to "persinfo"
 * 
 * 4. Run ScanPSNetTerms.java, to get latest Topic ID and name from PSNet
 * 		input: Internet-PSNet
 * 		output: PSNet raw taxonomy.txt
 * 
 * 5. Manually extract the hierarchical topics of the 6 perspectives to PSNet raw taxonomy_adLevels.xls, 6 subsheets.
 * 		input: PSNet raw taxonomy.txt & PSNet latest taxonomy tree at https://psnet.ahrq.gov/topics
 * 		output: PSNet raw taxonomy_adLevels.xls with 6 subsheets
 * 
 * 6. Run LinkCaseIDToTopicID.java, to link caseIDs to each Topic
 * 		input: PSNet raw taxonomy.txt
 * 		output: 1_Approach to Improving Safety.txt
 * 				2_Clinical Area.txt
 * 				3_Error Types.txt
 * 			 	4_Safety Target.txt
 * 				5_Setting of Care.txt
 * 				6_Target Audience.txt
 * 
 * 7. Run SQL_tree_fingerprint.java, to write a SQL file for "treeinfo" and "casefinger"
 * 		input: 1_Approach to Improving Safety.txt
 * 			   2_Clinical Area.txt
 * 			   3_Error Types.txt
 * 			   4_Safety Target.txt
 * 			   5_Setting of Care.txt
 * 			   6_Target Audience.txt
 * 		output: sql_PSNet/tree_fingerprint.sql
 * 				treeSQL_backup.txt
 * 				caseFingerprintSQL_backup.txt
 * 
 * 8. Run sql_PSNet/tree_fingerprint.sql at MYSQL Workbench, to import data to "treeinfo" and "casefinger"
 * 		input: sql_PSNet/tree_fingerprint.sql
 * 		output: Tables "treeinfo" and "casefinger"
 * 
 * 9. Run similarity_vs/CreateSQLFile_vs.java, to generate SQL file for the 6 similarity matrices
 * 		input: table "casefinger" in webmm database
 * 		output: sql_PSNet/Matrix_vs.sql
 * 				Matrix_vs_1_backup.txt
 * 				Matrix_vs_2_backup.txt
 * 				Matrix_vs_3_backup.txt
 * 				Matrix_vs_4_backup.txt
 * 				Matrix_vs_5_backup.txt
 * 				Matrix_vs_6_backup.txt
 * 
 * 10. Run sql_PSNet/Matrix_vs.sql, to import the 6 similarity matrices to webmm database
 * 		input: sql_PSNet/Matrix_vs.sql
 * 		output: 6 similarity matrices in webmm database
 * 
 * 
 * ### UPDATING ###
 * 
 * ** three manually update files:
 * 		PSNet/case name.xls
 * 		PSNet/perspective.xls
 * 		PSNet raw taxonomy_adLevels.xls 
 * 
 * 1. Only update cases and perspectives
 * 		1-2-3-6-7-8-9-10 (NO 4, 5)
 * 
 * 2. Only update Topics
 * 		4-5-6-7-8-9-10 (NO 1, 2, 3)
 * 
 * 3. Update all
 * 		Run all steps * 				
 * 		
 */
package database_PSNet;

public class Readme {

}
