
    function getTable() {
        return document.getElementById("techniqueTable");
    }

    /** @return the number of the select dropdown. There is one for each step in the technique. */
    function getStepNumber(selectId)  {
        var elSelect = document.getElementById(selectId);
        var index = elSelect.id.indexOf("_select");
        var sNum = elSelect.id.substring(4, index);
        return parseInt(sNum);
    }

    /**
     * When called, we delete all future selects (and corresponding img) and create a single next one.
     */
    function selectChanged(selectId) {

        var stepNum = getStepNumber(selectId);
        var selectedVal = getSelectedValue(selectId);

        var table = getTable();
        var selectRow = table.rows[0];
        var imageRow = table.rows[1];

        deleteFutureSelects(selectRow, imageRow, stepNum);
        setBigImage(imageRow, stepNum, selectedVal);

        var tdSelect = document.createElement("td");
        var newSelect = createNewSelector(stepNum);

        var nextSelectOptions = next[selectedVal];
        var onlyOneChild = false;
        if (nextSelectOptions) {
            onlyOneChild = true;
            if (nextSelectOptions.length > 1) {
                onlyOneChild = false;
                // the first one is -----;
                option = new Option("option");
                var nextOpt = "-----";
                option.value = nextOpt;
                option.textContent = nextOpt;
                newSelect.appendChild(option);
            }
            for (var i=0; i < nextSelectOptions.length; i++) {
                option = new Option("option");
                var nextOpt = nextSelectOptions[i];
                option.value = nextOpt;
                option.textContent = label[nextOpt];
                //alert("about to add "+option.outerHTML);
                newSelect.appendChild(option);
            }
        }
        else {
            return;
        }

        tdSelect.appendChild(newSelect);
        var tdImage = document.createElement("td");

        var newImage = createNewImage(stepNum, onlyOneChild, nextSelectOptions, selectedVal);
        var newImageAnchor = createImageAnchor(newImage);
        tdImage.appendChild(newImageAnchor);

        selectRow.insertBefore(tdSelect, selectRow.children[stepNum + 1]);
        imageRow.insertBefore(tdImage, imageRow.children[stepNum + 1]);
        if (onlyOneChild) { // add the next one too
            selectChanged(newSelect.id);
        }
    }

    /** @return the currently selected value */
    function getSelectedValue(selectId) {
        var elSelect = document.getElementById(selectId);
        return elSelect.options[elSelect.selectedIndex].value;
    }

    /**
     *  Delete future selects. Delete steps up to the final filler td.
     */
    function deleteFutureSelects(selectRow, imageRow, stepNum) {
        var len = selectRow.children.length;
        for (var i = len-2; i > stepNum; i--) {
            selectRow.removeChild(selectRow.children[i]);
            imageRow.removeChild(imageRow.children[i]);
        }
    }

    /** Set the current large image at the bottom */
    function setBigImage(imageRow, stepNum, selectedVal) {
        var currentImage = imageRow.children[stepNum].children[0].children[0];
        if (selectedVal == '-----') {
            currentImage.src = 'images/select.png';
            currentImage.title = "select something from the dropdown";
        } else {
            currentImage.src = img[selectedVal];
            currentImage.title = desc[selectedVal];
        }
    }

    /** create a new selector fpr the next step and its corresponding image  */
    function createNewSelector(stepNum) {
        var newSelect = document.createElement("select");
        var newSelectId = 'step' + (stepNum + 1) + '_select'
        newSelect.setAttribute('id', newSelectId);
        newSelect.onchange = function anonymous() { selectChanged( newSelectId ); };
       return newSelect;
    }

    /**
     * @param stepNum the nth step of the technique
     * @param onlyOneChild true if there is exactly one child.
     *   In that case if will be automatically shown because there are no other choices.
     * @param nextSelectOptions the options for the next step's deopdown.
     * @param selectedVal id of currently selected technique step.
     * @return a new small image with specified id at the specified step
     */
    function createNewImage(stepNum, onlyOneChild, nextSelectOptions, selectedVal) {
        var imageId = 'step' + (stepNum + 1) + '_image';
        var image = document.createElement("img");
        image.setAttribute('id', imageId);
        image.setAttribute("title", onlyOneChild ? desc[selectedVal] : "Select something");
        image.setAttribute('src', onlyOneChild ? img[nextSelectOptions[0]] : 'images/select.png');
        image.setAttribute('width', '170');
        image.setAttribute('height', '130');
        image.setAttribute("border", 0);
        return image;
    }

    /** return new image anchor tag */
    function createImageAnchor(image) {
        var imageAnchor = document.createElement("a");
        imageAnchor.onmouseover = function anonymous() { mousedOnThumbnail(image.id); };
        imageAnchor.appendChild(image);
        return imageAnchor;
    }

    /** for debugging */
    function showVals(selectedVal, valuesList) {
        var textList = "selectedVal=" + selectedVal + "\n";
        for (var i=0; i < valuesList.length; i++) {
           textList += valuesList[i] + "\n";
        }
        alert(textList);
    }

    /** show a big image when mousing over the thumbnail */
    function mousedOnThumbnail(imgId) {
        var elImg = document.getElementById(imgId);
        var bigImg = document.getElementById('big_image');
        bigImg.src = elImg.src;
        bigImg.title = elImg.title;
    }

    /**
     * Called when page loads.
     * Initializes the menu for the first step with the possible attacks
     */
    function doOnLoad() {
        var initialSelect = document.getElementById("step0_select");

        option = new Option("option");
        var nextOpt = "-----";
        option.value = nextOpt;
        option.textContent = nextOpt;
        initialSelect.appendChild(option);

        for (var i=0; i<attacks.length; i++) {
            option = new Option("option");
            var nextOpt = attacks[i];
            option.value = nextOpt;
            option.textContent = label[nextOpt];
            initialSelect.appendChild(option);
        }
    }
