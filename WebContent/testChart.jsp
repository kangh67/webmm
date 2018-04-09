<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
String[] title = new String[5];
title[0] = "A";
title[1] = "B";
title[2] = "C";
title[3] = "D";
title[4] = "E";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">

          // Load the Visualization API and the piechart package.
          google.load('visualization', '1.0', {'packages':['corechart']});

          // Set a callback to run when the Google Visualization API is loaded.
          google.setOnLoadCallback(drawChart);

          // Callback that creates and populates a data table,
          // instantiates the pie chart, passes in the data and
          // draws it.
          function drawChart() {

            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Topping');    
            data.addColumn('number', 'Slices');
            data.addRows([
              ['Mushrooms', 3],
              ['Onions', 1],
              ['Olives', 1],
              ['Zucchini', 1],
              ['Pepperoni', 2],
              ['Pepperoni2', 2],
              ['Pepperoni3', 2],
              ['Pepperoni4', 2],
              ['Pepperoni5', 2],
              ['Pepperoni6', 2],
              ['Pepperoni7', 2],
              ['Pepperoni8', 2],
              ['Pepperoni9', 2],
              ['Pepperoni10', 2],
              ['Pepperoni11', 2],
              ['Pepperoni12', 2],
              ['Pepperoni13', 2],
              ['Pepperoni14', 2],
              ['Pepperoni15', 2],
              ['Pepperoni16', 2]
              
            ]);
            // Create the data table.
            var data2 = new google.visualization.DataTable();
            data2.addColumn('string', 'Topping');
            data2.addColumn('number', 'Slices');
            data2.addRows([
              ["Mushrooms", 3],
              ["Oni'ons", 1],
              ["Olives", 15],
              ["Zucchini", 1],
              ["Pepperoni", 2]
            ]);

            var data3 = new google.visualization.DataTable();
            data3.addColumn('string', 'Year');
            data3.addColumn('number', 'Sales');
            data3.addColumn('number', 'Expenses');
            data3.addRows([
              ['2004', 1000, 400],
              ['2005', 1170, 460],
              ['2006',  860, 580],
              ['2007', 1030, 540]
            ]);

            // Set chart options
            var options = {'title':'How Much Pizza I Ate Last Night',
                           'width':400,
                           'height':300};
            // Set chart options
            var options2 = {'title':'How Much Pizza You Ate Last Night',
                           'width':400,
                           'height':300};
            // Set chart options
            var options3 = {'title':'Line chart',
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
            chart.draw(data, options);
            var chart2 = new google.visualization.PieChart(document.getElementById('chart_div2'));
            chart2.draw(data2, options2);
            var chart3 = new google.visualization.LineChart(document.getElementById('chart_div3'));
            chart3.draw(data3, options3);

          }
        </script>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<div id="chart_div"></div>
        <div id="chart_div2"></div>
        <div id="chart_div3"></div>
</body>
</html>