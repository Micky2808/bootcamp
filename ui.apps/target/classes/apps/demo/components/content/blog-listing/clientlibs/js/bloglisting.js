$(document).ready(function() {
   $('.btn-showmore').on("click",function() {
        var resourcePath = $('.blog-listing-main').attr('data-res-path');
        resourcePath = resourcePath.replace('jcr:content',"_jcr_content");
        var noOfBlogsDisplayed = $('.blog-listing-main .4u').length;
        var totalResults = $('.blog-listing-main').attr('data-total-res');
        $.ajax({
            type:"GET",
            url: resourcePath + ".blogthumbs." + noOfBlogsDisplayed + ".escape." + "json",
            success: function (responseJson) {
                var output = "";

                for (var i=0; i < responseJson.length; i++) {
                    output = output + '<div class="4u 6u(2) 12u$(3)"><article class="box post"><a href="' + responseJson[i].path + '" class="img fit"><img src="' + responseJson[i].image + '" alt=""></a><h3>' + responseJson[i].title + '</h3><p>' + responseJson[i].description + '</p><ul class="actions"><li><a href="' + responseJson[i].path + '" class="button">'+$('.blog-listing-main').attr('data-button-text')+'</a></li></ul></article></div>';
                }

                $(".blog-listing-main").append(output);

                var totalDisplayed = $('.blog-listing-main .4u').length;
                if(totalDisplayed >= Number(totalResults)){
                    $(".btn-showmore").hide();
                }
            }
        });
    });
});