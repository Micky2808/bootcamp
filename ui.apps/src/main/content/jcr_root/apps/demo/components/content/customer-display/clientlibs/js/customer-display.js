/*This is the customer listing java script file*/

$(document).ready(function() {
$.ajax({
    type:"GET",
    url: "/customer/node"+".escape."+ "json",
    success: function (responseJson) {
        var output = "";

        for(var i = 0; i < responseJson.length ; i++)
        {
            output = output + ' <tr class="row-disp"><td>'+responseJson[i].customerId+'</td><td>'+responseJson[i].name+'</td><td>'+responseJson[i].shippingAddress+'</td><td>'+responseJson[i].state+'</td><td>'+responseJson[i].zipCode+'</td><td>'+responseJson[i].email+'</td><tr/>';
        }
        $('.customerTableBody').html(output);
        var rowsShown = 5;
        var rowsTotal = $('.row-disp').length;
        var numPages = rowsTotal/rowsShown;
        for(i = 0;i < numPages;i++) {
            var pageNum = i + 1;
            $('#index-list').append('<a href="#" rel="'+i+'">'+pageNum+'</a> ');
        }
        $('.row-disp').hide();
        $('.row-disp').slice(0, rowsShown).show();
        $('#index-list a:first').addClass('active');

        $('#index-list a').click(function(){
            $('#index-list a').removeClass('active');
            $(this).addClass('active');
            var currPage = $(this).attr('rel');
            var startItem = currPage * rowsShown;
            var endItem = startItem + rowsShown;
            $('#table-data tbody .row-disp').css('opacity','0.0').hide().slice(startItem, endItem).css('display','table-row').animate({opacity:1}, 300);
        });
    },
    error:{
		
    }
});
/* uncomment to generate .xls format output

      $('.export-excel').on("click", function(){
          var tableID = 'table-data';
          var filename = '';
          var downloadLink;
          var dataType = 'application/vnd.ms-excel';
          var tableSelect = document.getElementById(tableID);
          var tableHTML = tableSelect.outerHTML.replace(/ /g, '%20');

          // Specify file name
          filename = filename?filename+'.xls':'excel_data.xls';

          // Create download link element
          downloadLink = document.createElement("a");

          document.body.appendChild(downloadLink);

          if(navigator.msSaveOrOpenBlob){
              var blob = new Blob(['\ufeff', tableHTML], {
                  type: dataType
              });
              navigator.msSaveOrOpenBlob( blob, filename);
          }else{
              // Create a link to the file
              downloadLink.href = 'data:' + dataType + ', ' + tableHTML;

              // Setting the file name
              downloadLink.download = filename;

              //triggering the function
              downloadLink.click();
          }
      });

*/
      $('.export-pdf').on("click", function(){
            $('#table-data').tableHTMLExport({
              // csv, txt, json, pdf
              type:'pdf',
              // file name
              filename:'export.pdf'
            });
      });

      $('.export-excel').on("click", function(){
                  $('#table-data').tableHTMLExport({
                    // csv, txt, json, pdf
                    type:'csv',
                    // file name
                    filename:'export.csv'
                  });
      });
});



