
    function getTable() {
        return document.getElementById("techniqueTable");
    }

    /**
     * When called, we delete all future selects (and corresponding img) and create a single next one.
     */
    function selectChanged(selectId) {
        var elSelect = document.getElementById(selectId);
        var index = elSelect.id.indexOf("_select");
        var sNum = elSelect.id.substring(4, index);
        var stepNum = parseInt(sNum);
        selectedVal = elSelect.options[elSelect.selectedIndex].value;
        valuesList = next[selectedVal];

        var table = getTable();
        var selectRow = table.rows[0];
        var imageRow = table.rows[1];
        var fillerRow = table.rows[2];
        //fillerRow.children[0].setAttribute("colspan", stepNum + 1);
        fillerRow.children[0].colspan = stepNum + 1;

        // delete future selects
        var len = selectRow.children.length;
        //alert("elSelect.id=" + elSelect.id + " sNum=" + sNum + " stepNum=" + stepNum
        //+ " len=" + len + " len-stepNum-2="+(len-stepNum-2)+" selectRow.children="+selectRow.children
        //+" imageRow=" + imageRow);

        // delete steps up to the final filler td
        for (var i=len-2; i>stepNum; i--) {
            selectRow.removeChild(selectRow.children[i]);
            imageRow.removeChild(imageRow.children[i]);
        }

        //alert("imageRow=" + imageRow + " stepNum=" + stepNum
        //   + " imageRow.children[stepNum]=" + imageRow.children[stepNum]);

        var currentImage = imageRow.children[stepNum].children[0].children[0];
        if (selectedVal == '-----') {
            currentImage.src = 'images/select_s.png';
        } else {
            currentImage.src = img[selectedVal];
        }

        // add the new select and corresponding image
        var tdSelect = document.createElement("td");
        var newSelect = document.createElement("select");
        var newSelectId = 'step' + (stepNum+1) + '_select'
        newSelect.setAttribute('id', newSelectId);
        newSelect.onchange = function anonymous() { selectChanged( newSelectId ); };

        var nextSelectOptions = next[selectedVal];
        //alert("nextSelectOptions=" + nextSelectOptions);
        var onlyOneChild = false;
        if (nextSelectOptions) {
            onlyOneChild = true;
            if (nextSelectOptions.length > 1) {
                onlyOneChild = false;
                // the first one is -----;
                option = document.createElement("option");
                var nextOpt = "-----";
                option.value = nextOpt;
                option.innerText = nextOpt;
                newSelect.appendChild(option);
            }
            for (var i=0; i<nextSelectOptions.length; i++) {
                option = document.createElement("option");
                var nextOpt = nextSelectOptions[i];
                option.value = nextOpt;
                option.innerText = label[nextOpt];
                //alert("about to add "+option.outerHTML);
                newSelect.appendChild(option);
            }
        }
        else {
            return;
        }

        tdSelect.appendChild(newSelect);

        // and image
        var tdImage = document.createElement("td");
        var newImageAnchor = document.createElement("a");
        var newImage = document.createElement("img");
        var imageId = 'step'+(stepNum+1)+'_image';
        newImageAnchor.onmouseover =  function anonymous() { mousedOnThumbnail(imageId); };
        newImage.setAttribute('id', imageId);
        newImage.setAttribute('src', onlyOneChild?img[nextSelectOptions[0]]:'images/select_s.png');
        newImage.setAttribute('width', '170');
        newImage.setAttribute('height', '130');

        newImage.setAttribute("border", 0);
        newImageAnchor.appendChild(newImage);
        tdImage.appendChild(newImageAnchor);

        selectRow.insertBefore(tdSelect, selectRow.children[stepNum+1]);
        imageRow.insertBefore(tdImage, imageRow.children[stepNum+1]);
        if (onlyOneChild) { // add the next one too
            selectChanged(newSelectId);
        }
    }

    // for debugging
    function showVals(selectedVal, valuesList) {
        var textList = "selectedVal=" + selectedVal + "\n";
        for (var i=0; i<valuesList.length; i++) {
           textList += valuesList[i] + "\n";
        }
        alert(textList);
    }

    // show a big image when mousing over the thumbnail
    function mousedOnThumbnail(imgId) {
        var elImg = document.getElementById(imgId);
        var bigImg = document.getElementById('big_image');
        var newSrc = elImg.src.replace("_s.", "_m.");
        bigImg.src = newSrc;
    }

    // called when page loads
    function doOnLoad() {
    }
