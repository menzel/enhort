/**
* Created by menzel on 16/2/16.
*/


function plotHistogram(results) {
    var data = [];

    for (var pack in results) {

        var vals = [];
        for (var v in results[pack]) {
            vals.push(results[pack][v].effectSize)
        }

        data.push({
            x: vals,
            name: pack,
            autobinx: true,
            histnorm: "count",
            marker: {
                color: '#' + Math.random().toString(16).substr(-6),
                line: {
                    color: "rgba(255, 100, 102, 1)",
                    width: 1
                }
            },
            opacity: 0.5,
            type: "histogram"
        });
    }

    var layout = {
        barmode: "overlay",
        title: "Histogram of effect sizes by annotation group",
        xaxis: {title: "Effect size"},
        yaxis: {title: "Count"}
    };

    Plotly.newPlot('histogram', data, layout);

}


function plotBubble(names, pca) {

    var size = 5;

    var myConfig1 = {
        "type": "scatter",
        "tooltip": {
            "text": "%data-filenames"
        },
        "series": [{
            "values": pca,
            "text": "Blue",
            "data-filenames": names

        }, {
            "values": [[0, 0]],
            "text": "Red",
            "data-filenames": ['Your positions']
        }],
        scaleY: {
            lineColor: "none",
            tick: {
                visible: false
            },
            guide: {
                visible: true
            },
            item: {
                visible: false
            }
        },
        scaleX: {
            lineColor: "none",
            tick: {
                visible: false
            },
            guide: {
                visible: true
            },
            item: {
                visible: false
            }
        }
    };

    zingchart.render({
        id: 'scatterChart',
        data: myConfig1,
        height: "100%",
        width: "100%"
    });
}

function plotRadar(efs) {

    var vals = [];
    var labels = [];

    for (var pack in efs) {
        vals.push(efs[pack][4]);
        labels.push(pack)
    }

    var myConfig = {
        "type": "radar",
        "plot": {
            "aspect": "area"
        },
        "scale-k": {
            "labels": labels,
            "item": {
                "font-color": "black",
                "font-family": "Georgia",
                "font-size": 14
            },
            "guide": {
                "line-color": "blue",
                "line-width": 1,
                "line-style": "solid",
                "background-color": "#f0f0f5"
            }
        },
        "scale-v": {
            "visible": true
        },
        "series": [{
            "values": vals,
            text: 'HIV',
            backgroundColor: 'blue'
        }]
    };

    zingchart.render({
        id: 'radarChart',
        data: myConfig,
        height: '100%',
        width: '100%'
    });
}

function plotNames(results) {
    var limit = 0.2; /* Limit for fold change of named track plot */

    for (var j = 0; j < results.length; j++) {
        var result = results[j];


        var y1 = [];
        var y2 = [];

        for (var key in result.namesExp) {

            if (result.namesMea.hasOwnProperty(key)) {

                var effectSize = result.namesExp[key] / result.namesMea[key];

                var bgcount = result.expectedIn + result.expectedOut;
                var posCount = result.measuredIn + result.measuredOut;

                if (effectSize >= 1.0 + limit || effectSize <= 1.0 - limit) {
                    y1.push(result.namesExp[key] / bgcount);
                    y2.push(result.namesMea[key] / posCount);
                }
            }
        }


        if (y1.length < 2 || y1.length > 1000) {
            continue;
        }

        var trace1 = {
            x: Object.keys(result.namesExp),
            y: y1,
            name: 'Expected',
            type: 'bar',
            marker: {color: 'rgb(140,140,140)'},
        };

        var trace2 = {
            x: Object.keys(result.namesMea),
            y: y2,
            name: 'Measured',
            type: 'bar',
            marker: {color: 'rgb(51,122,183)'},
        };

        var data = [trace1, trace2];

        var layout = {
            width: 600,
            height: 200,
            barmode: 'group',
            xaxis: {
                autorange: true,
                showgrid: false,
                zeroline: true,
                showline: false,
                autotick: true,
                ticks: '',
                showticklabels: false
            },
            yaxis: {
                autorange: true,
                showgrid: false,
                zeroline: false,
                showline: false,
                autotick: true,
                ticks: '',
                showticklabels: false
            },
            margin: {l: 20, r: 20, b: 40, t: 0, pad: 0},
            showlegend: false,
            barnorm: "fraction"
        };
        Plotly.newPlot('nameplot_' + result.id, data, layout);
    }
}


function plot(results) {
    var buttons_s = {};

    for (var j = 0; j < results.length; j++) {

        var x0 = results[j].scoresMea;
        var x1 = results[j].scoresExp;

        if (x0 === null || x0.length < 3 || x1.length < 3) {
            break;
        }

        var measured = {
            x: x0,
            marker: {color: 'rgb(51,122,183)'},
            opacity: 0.75,
            type: 'histogram',
            name: "measured"
        };

        var expected = {
            x: x1,
            opacity: 0.75,
            marker: {color: 'rgb(140,140,140)'},
            type: 'histogram',
            name: "expected"
        };
        var data_s = [measured, expected];

        var layout_s = {
            autosize: false,
            width: 400,
            height: 100,
            barmode: 'overlay',
            xaxis: {
                autorange: true,
                showgrid: true,
                zeroline: true,
                showline: false,
                autotick: true,
                ticks: ''
            },
            yaxis: {
                autorange: true,
                showgrid: false,
                zeroline: false,
                showline: false,
                autotick: true,
                ticks: '',
                showticklabels: false
            },
            margin: {l: 0, r: 0, b: 15, t: 0, pad: 0}
        };


        if (results[j].name.startsWith("Distance from")) { //if is 'distance to' track
            layout_s.yaxis.type = 'log'
        }
        Plotly.newPlot("plot_" + results[j].id, data_s, layout_s, buttons_s);

    }
}


function setSequenceLogo(logo, div) {
    var basewidth = document.body.offsetWidth/3;

    var margin = {top: 10, right: 20, bottom: 30, left: 50},
        width = basewidth - margin.left - margin.right,
        height = 150 - margin.top - margin.bottom;

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left");

    var svg = d3.select(div).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    sequencelogoFont();

    var data = JSON.parse(logo);

    data.forEach(function (d) {
        var y0 = 0;
        d.bits = d.map(function (entry) {
            return {bits: entry.bits, letter: entry.letter, y0: y0, y1: y0 += +entry.bits};
        });

        d.bitTotal = d.bits[d.bits.length - 1].y1;
    });

    x.domain(data.map(function (d, i) {
        return i;
    }));

    var maxBits = d3.max(data, function (d) {
        return d.bitTotal
    });

    y.domain([0, maxBits]);

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Bits");

    var column = svg.selectAll(".sequence-column")
        .data(data)
        .enter()
        .append("g")
        .attr("transform", function (d, i) {
            return "translate(" + (x(i) + (x.rangeBand() / 2)) + ",0)";
        })
        .attr("class", "sequence-column");

    var capHeightAdjust = 1.6; // approximation to bring cap-height to full font size

    column
        .selectAll("text")
        .data(function (d) {
            return d.bits;
        })
        .enter()
        .append("text")
        .attr("y", function (e) {
            return y(e.y0);
        })
        .text(function (e) {
            return e.letter;
        })
        .attr("class", function (e) {
            return "letter-" + e.letter;
        })
        .style("text-anchor", "middle")
        .style("font-family", "sequencelogo")
        .attr("textLength", x.rangeBand())
        .attr("lengthAdjust", "spacingAndGlyphs")
        .attr("font-size", function (e) {
            return ( y(e.y0) - y(e.y1) ) * capHeightAdjust;
        })
        .style("font-size", function (e) {
            return (( y(e.y0) - y(e.y1) ) * capHeightAdjust) + "px";
        })
    ;


    function sequencelogoFont() {
        var font = svg.append("defs").append("font")
            .attr("id", "sequencelogo")
            .attr("horiz-adv-x", "1000")
            .attr("vert-adv-y", "1000");

        font.append("font-face")
            .attr("font-family", "sequencelogo")
            .attr("units-per-em", "1000")
            .attr("ascent", "950")
            .attr("descent", "-50");

        font.append("glyph")
            .attr("unicode", "A")
            .attr("vert-adv-y", "50")
            .attr("d", "M500 767l-120 -409h240zM345 948h310l345 -1000h-253l-79 247h-338l-77 -247h-253l345 1000v0z");

        font.append("glyph")
            .attr("unicode", "C")
            .attr("vert-adv-y", "50")
            .attr("d", "M1000 -6q-75 -23 -158 -34.5t-175 -11.5q-325 0 -496 128.5t-171 370.5q0 244 171 372.5t496 128.5q92 0 176 -12t157 -35v-212q-82 46 -159 66q-77 22 -159 22q-174 0 -263 -84q-89 -82 -89 -246q0 -162 89 -246q89 -82 263 -82q82 0 159 20q77 22 159 67v-212v0z");

        font.append("glyph")
            .attr("unicode", "G")
            .attr("vert-adv-y", "50")
            .attr("d", "M745 141v184h-199v160h454v-442q-84 -47 -186 -71q-100 -24 -216 -24q-286 0 -442 129q-156 131 -156 370q0 244 157 372q158 129 455 129q89 0 175 -17q86 -16 161 -47v-211q-62 51 -141 77q-79 27 -174 27q-166 0 -248 -82t-82 -248q0 -160 79 -244t230 -84q45 0 79 5 q34 6 54 17v0z");

        font.append("glyph")
            .attr("unicode", "T")
            .attr("vert-adv-y", "50")
            .attr("d", "M640 -52h-280v827h-360v173h1000v-173h-360v-827v0z");
    }
}

