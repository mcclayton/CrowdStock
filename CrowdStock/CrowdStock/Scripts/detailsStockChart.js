$("#up_arrow").click(up_request);
$("#down_arrow").click(down_request);

function up_request () {
console.log("Up");
}
function up_request () {
console.log("Down");
}

$(function getData () {
    $.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=aapl-c.json&callback=?', function (data) {
        // Create the chart
        $('#container').highcharts('StockChart', {
            rangeSelector : {
                selected : 1,
                inputEnabled: $('#container').width() > 480
            },

            title : {
                text : 'AAPL Stock Price'
            },

            series : [{
                name : 'AAPL',
                data : data,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    });
});