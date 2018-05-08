function plot_circos(hotspots, names, counts, width, div_id) {

    let chromosomes = [
        {"id": "chr1", "label": "1", "len": 249250621, "color": "white"},
        {"id": "chr2", "label": "2", "len": 243199373, "color": "white"},
        {"id": "chr3", "label": "3", "len": 198022430, "color": "white"},
        {"id": "chr4", "label": "4", "len": 191154276, "color": "white"},
        {"id": "chr5", "label": "5", "len": 180915260, "color": "white"},
        {"id": "chr6", "label": "6", "len": 171115067, "color": "white"},
        {"id": "chr7", "label": "7", "len": 159138663, "color": "white"},
        {"id": "chr8", "label": "8", "len": 146364022, "color": "white"},
        {"id": "chr9", "label": "9", "len": 141213431, "color": "white"},
        {"id": "chr10", "label": "10", "len": 135534747, "color": "white"},
        {"id": "chr11", "label": "11", "len": 135006516, "color": "white"},
        {"id": "chr12", "label": "12", "len": 133851895, "color": "white"},
        {"id": "chr13", "label": "13", "len": 115169878, "color": "white"},
        {"id": "chr14", "label": "14", "len": 107349540, "color": "white"},
        {"id": "chr15", "label": "5", "len": 102531392, "color": "white"},
        {"id": "chr16", "label": "16", "len": 90354753, "color": "white"},
        {"id": "chr17", "label": "17", "len": 81195210, "color": "white"},
        {"id": "chr18", "label": "18", "len": 78077248, "color": "white"},
        {"id": "chr19", "label": "19", "len": 59128983, "color": "white"},
        {"id": "chr20", "label": "20", "len": 63025520, "color": "white"},
        {"id": "chr21", "label": "21", "len": 48129895, "color": "white"},
        {"id": "chr22", "label": "22", "len": 51304566, "color": "white"},
        {"id": "chrX", "label": "X", "len": 155270560, "color": "white"},
        {"id": "chrY", "label": "Y", "len": 59373566, "color": "white"}
    ];


    let blocks = [];
    let blocksize = 15478387;

    let chrmap = {};
    let sum = 0;

    // get chromosome for given position
    function getchr(pos) {
        let chrpos = 0;

        for (let i = 0; i < 24; i++) {
            chrpos += chromosomes[i]['len'];

            if (pos < chrpos) {
                return chromosomes[i]['id'];
            }
        }

        return "unkown";
    }

    // cum sum for chromosomes
    for (let i = 0; i < 24; i += 1) {
        chrmap[chromosomes[i]['id']] = sum;
        sum += chromosomes[i]['len'];
    }

    // create blocks list
    for (let i = 0; i < 200; i += 1) {
        let pos = i * blocksize;
        let chr = getchr(pos);
        blocks.push({
            block_id: chr,
            start: pos - chrmap[chr],

            end: pos - chrmap[chr] + blocksize - 1,
            value: hotspots[i]
        });
    }

    let snp250 = blocks.map(function (d) {
        return {
            block_id: d.block_id,
            position: (parseInt(d.start) + parseInt(d.end)) / 2,
            value: d.value
        }
    });

    let circosHighlight = new Circos({
        container: "#" + div_id,
        width: width,
        height: width,
    });

    let colors = [
        'rgba(227,123,64,',
        'rgba(70,178,157,',
        'rgba(222,91,73,',
        'rgba(50,77,92,',
        'rgba(240,202,77,',
        'rgba(137,178,85,',
    ];

    let plot = circosHighlight
        .layout(
            chromosomes,
            {
                outerRadius: width / 2 - 15,
                innerRadius: width / 2 - 15,
                gap: 0.01,
                labels: {
                    display: true,
                    size: '7px',
                    radialOffset: 6
                },
                ticks: {display: false}
            });

    plot = plot.scatter('line1', snp250, {
        innerRadius: 0.43,
        outerRadius: 0.53,
        color: 'black',
        direction: 'in',
        axes: [
            {
                color: '#8baee5',
                spacing: 5
            }
        ]
    });


    plot = plot.highlight('circle5', blocks, {
        innerRadius: 0.53,
        outerRadius: 0.6,
        color: function (d) {
            return colors[5] + counts[5][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[5]) + ')';
        },
        tooltipContent: function (d) {
            return names[5];
        }
    });

    plot = plot.highlight('circle4', blocks, {
        innerRadius: 0.61,
        outerRadius: 0.68,
        color: function (d) {
            return colors[4] + counts[4][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[4]) + ')';
        },
        tooltipContent: function (d) {
            return names[4];
        }
    });


    plot = plot.highlight('circle3', blocks, {
        innerRadius: 0.69,
        outerRadius: 0.76,
        color: function (d) {
            return colors[3] + counts[3][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[3]) + ')';
        },
        tooltipContent: function (d) {
            return names[3];
        }
    });


    plot = plot.highlight('circle2', blocks, {
        innerRadius: 0.77,
        outerRadius: 0.84,
        color: function (d) {
            return colors[2] + counts[2][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[2]) + ')';
        },
        tooltipContent: function (d) {
            return names[2];
        }
    });

    plot = plot.highlight('circle1', blocks, {
        innerRadius: 0.85,
        outerRadius: 0.92,
        color: function (d) {
            return colors[1] + counts[1][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[1]) + ')';
        },
        tooltipContent: function (d) {
            return names[1];
        }
    });

    plot = plot.highlight('circle0', blocks, {
        innerRadius: 0.93,
        outerRadius: 1.0,
        color: function (d) {
            return colors[0] + counts[0][(d.start + chrmap[d.block_id]) / blocksize] / Math.max(...counts[0]) + ')';
        },
        tooltipContent: function (d) {
            return names[0];
        }
    });
    plot.render();

}
