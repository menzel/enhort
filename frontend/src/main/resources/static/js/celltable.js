var rowheader = document.getElementsByClassName("rowheader");
var colheader = document.getElementsByClassName("trackname");
var coln = colheader.length;
var rown = rowheader.length - 1;



/*
Set given boolean val to all checkboxes in given row
 */
function checkRow(row, val) {

    for(var j=1; j<=coln; j++) { // first element is the row header, skip
        var child = row.nextElementSibling.parentElement.cells[j].firstElementChild;
        if(child !== null)
            child.checked = val;
    }
}

/*Select all in row if the rowname is clicked */

//iterate over table rows and add listeners
for(var i=0, l=rowheader.length; i<l; i++){
    rowheader[i].addEventListener('click', function (event) {
        var row = event.currentTarget;

        for (var j = 1; j <= coln; j++) { // first element is the row header, skip
            var child = row.nextElementSibling.parentElement.cells[j].firstElementChild;

            if (child !== null && child.checked === false) { //if any element is false:
                checkRow(row, true); //set all checkboxes to true
                break;
            }
        }

        if (j === coln+1) //if all elements are true, set all to false:
            checkRow(row, false);
    });
}

/*
Set the given val to all checkboxes with the given colnum
 */
function checkCol(rowheader, colnum, val) {

    for(var j = 0; j <= rown; j++) { //iterate over all rows

        if(rowheader[j].nextElementSibling === null)
            continue; //skip without td elemnts with possible checkboxes

        var child = rowheader[j].nextElementSibling.parentElement.cells[colnum].firstElementChild; // get elements from each row

        if(child !== null)
            child.checked = val;
    }
}

/*Select all in column if the colname is clicked */
for(i=0, l=coln; i<l; i++){
    colheader[i].addEventListener('click', function (event) {
        var colnum = event.currentTarget.parentElement.cellIndex; // colnumber clicked
        col = event.currentTarget;

        for(var j = 0; j <= rown; j++) { //iterate over all rows
            if(rowheader[j].nextElementSibling === null)
                continue; //skip header rows without td elemnts with possible checkboxes

            child = rowheader[j].nextElementSibling.parentElement.cells[colnum].firstElementChild; // get elements from each row

            if (child !== null && child.checked === false) {
                checkCol(rowheader, colnum, true);
                break; //break after all elements are set
            }
        }

        if(j === rown+1){ // if all elements are true, set all to false now:
            checkCol(rowheader, colnum, false);
        }

    });
}
