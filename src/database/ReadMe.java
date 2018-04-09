/**
 * 1. Run SQL_users.java, to create user form
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\users.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\users.sql
 * 
 * 2. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\users.sql in MySql workbench, create user table
 * 
 * 3. Run SQL_questions.java, to create question list
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\questions.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\questions.sql
 * 
 * 4. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\questions.sql in MySql workbench, create question table
 * 
 * 5. Run SQL_report_general.java, to create general info form
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\report_general.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_general.sql
 * 
 * 6. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_general.sql
 * 
 * 7. Run SQL_report_detail.java (set form = "fall"), to create fall info form
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_fall.sql
 * 
 * 8. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_fall.sql
 * 
 * 9. Run SQL_report_detail.java (set form = "pu"), to create pressure ulcer info form
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_pu.sql
 * 
 * 10. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_pu.sql
 * 
 * 11. Run SQL_contributing_factors_list.java, to create table contributing_factors_list
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\contributing factors\\contributing factors_list_for server.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_list.sql
 * 
 * 12. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_list.sql
 * 
 * 13. Run SQL_contributing_factors_str.java, to create table contributing_factors_str
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\contributing factors\\contributing factors_str_for server.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_str.sql
 * 
 * 14. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_str.sql
 * 
 * 15. Run SQL_contributing_factors_RE.java, to create table contributing_factors_RE
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\contributing factors\\contributing factors_RE_for server.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_RE.sql
 * 
 * 16. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\contributing_factors_RE.sql
 * 
 * 17. Run SQL_MCPS_2014.java, generate SQL file for PSO 2014 fall reports
 * 		Input: C:\\Users\\hkang1\\Google Drive\\Tina Data\\2014 Fall Report Form for UT.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\MCPS_fall_2014.txt
 * 				C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2014.sql
 * 
 * 18. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2014.sql, Insert PSO 2014 fall reports
 * 
 * 19. Run SQL_UM2919.java, generate SQL file for University of Missouri fall reports
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\report formQi.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\UM2019_fall.txt
 * 				C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\UM_fall_2919.sql
 * 
 * 20. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\UM_fall_2919.sql, Insert University of Missouri fall reports
 * 
 * 21. Run SQL_MCPS_2016.java, generate SQL file for PSO 2016 fall reports
 * 		Input: C:\\Users\\hkang1\\Google Drive\\Tina Data\\UT 2016 falls1.xls
 * 				C:\\Users\\hkang1\\Google Drive\\Tina Data\\UT 2016 falls2.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\MCPS_fall_2016.txt
 * 				C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2016.sql
 * 
 * 22. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2016.sql, Insert PSO 2016 fall reports
 * 
 * 23. Run SQL_solution.java two times: 1.setting type="fall", 2.Setting type="pu"
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solutions_fall_forSQL.xls
 * 				C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solutions_pu_forSQL.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_fall.sql
 * 				C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_pu.sql
 * 
 * 24. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_fall.sql
 * 			C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_pu.sql
 * 
 * 25. Run SQL_solution_cf.java, setting type="fall"
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solutions_fall_forSQL.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_fall_cf.sql;
 * 
 * 26. Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_fall_cf.sql, to update form contributing_factors_list with CF information
 *
 * 27. Run SQL_solution_resource.java
 * 		Input: C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solution_resource_forSQL.xls
 * 		Output: C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solution_resource.sql
 * 
 * 28 Run C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solution_resource.sql
 */
package database;

public class ReadMe {

}
