var rowheader = document.getElementsByClassName("rowheader");
var colheader = document.getElementsByClassName("trackname");
var coln = colheader.length;
var rown = rowheader.length;


/*Select all in row if the rowname is clicked */

//iterate over table rows and add listeners
for(var i=0, l=rowheader.length; i<l; i++){
    rowheader[i].addEventListener('click', function (event) {
        var row = event.currentTarget;

        //iterate over childs and toggle checkboxes
        for(var j=1; j<=coln; j++){ // first element is the row header, skip
            var child = row.nextElementSibling.parentElement.cells[j].firstElementChild;

            if(child !== null)
                child.checked = !child.checked;
        }
    });
}

/*Select all in column if the colname is clicked */


for(i=0, l=coln; i<l; i++){
    colheader[i].addEventListener('click', function (event) {
        var colnum = event.currentTarget.parentElement.cellIndex; // colnumber clicked
        col = event.currentTarget;

        for(var j = 0; j <= rown; j++){ //iterate over all rows
            child = rowheader[j].nextElementSibling.parentElement.cells[colnum].firstElementChild; // get elements from each row

            if(child !== null)
                child.checked = !child.checked;

        }
    });
}

