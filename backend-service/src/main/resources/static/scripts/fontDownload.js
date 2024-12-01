const BASE_URL = window.location.protocol +'//'+ window.location.host
const font_save_url = BASE_URL+  '/fontFamily/api/v1/save-font-family ';
const tag_categories_get_url = BASE_URL + '/tagCategory/api/v1/get-all';  

        async function handleFile(event) {
            document.getElementById('fileInput').style.display = 'none';
            var fontFamilyDto =
            {
                title: "",
                fonts: [

                ]
            };

            var fontDto =
            {
                tags: [],
                type: "",
                fontFile: ""
            }

            var tagDto = {
                title: "",
                categoryId: 0
            }

            const fileInput = event.target;
            const file = fileInput.files[0];
            var tagCategories = await getTagCategories();
            var tagCategoryTags = gettagCategoryTags();
            
            console.log(tagCategories);
            if (file) {
                const reader = new FileReader();

                reader.onload = function (e) {

                  
                    const arrayBuffer = e.target.result;
                    const font = opentype.parse(arrayBuffer);
                    const uint8Array = new Uint8Array(arrayBuffer);

                    console.log(font);

                    //fontFamilyDto.title = font.names.fontFamily.en; --wrong? 
                    //fontFamilyDto.title = font.names.preferredFamily.en;

                    if (font.names.preferredFamily != undefined)
                    {
                        fontFamilyDto.title = font.names.preferredFamily.en
                    }
                    else 
                    {
                        fontFamilyDto.title = font.names.fontFamily.en;
                    }

                    fontDto.type = font.names.fontSubfamily.en;             
                    
                    if (font.tables.gpos!= undefined)
                    {   
                        var tag = '';
                        var categoryId = 0;
                        for (i = 0; i < font.tables.gpos.features.length; i++)
                        {
                            tag = font.tables.gpos.features[i].tag;
                            for (key in tagCategoryTags){
                                for (j = 0; j<tagCategoryTags[key].length; j++)
                                {
                                    if (tagCategoryTags[key][j] == tag){
                                        for (k = 0; k < tagCategories.length; k++)
                                        {
                                            if (key == tagCategories[k].title)
                                            {
                                                fontDto.tags.push({title: tag , categoryId: tagCategories[k].id})
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if ( font.tables.gsub != undefined)
                    {
                        var tag = '';
                        var categoryId = 0;
                        for (i = 0; i < font.tables.gsub.features.length; i++)
                        {
                            tag = font.tables.gsub.features[i].tag;
                            for (key in tagCategoryTags){
                                for (j = 0; j<tagCategoryTags[key].length; j++)
                                {
                                    if (tagCategoryTags[key][j] == tag){
                                        for (k = 0; k < tagCategories.length; k++)
                                        {
                                            if (key == tagCategories[k].title)
                                            {
                                                fontDto.tags.push({title: tag , categoryId: tagCategories[k].id})
                                            }
                                        }
                                    }
                                }
                            }                            
                        }
                    }

                    const base64String = arrayBufferToBase64(uint8Array);

                    console.log("Base64:", base64String);
                    fontDto.fontFile = base64String;

                    fontFamilyDto.fonts.push(fontDto);
                    console.log(fontFamilyDto);
                    clearForm(fileInput);
                    document.getElementById('fileInput').style.display = 'block';
                    sendFont(fontFamilyDto);
                    

                };

                reader.readAsArrayBuffer(file);
            }
        }

        function arrayBufferToBase64(arrayBuffer) {
            let binary = '';
            const bytes = new Uint8Array(arrayBuffer);

            for (let i = 0; i < bytes.length; i++) {
                binary += String.fromCharCode(bytes[i]);
            }

            return window.btoa(binary);
        }
        
        function clearForm(inputElement) {
            
            inputElement.value = null;
        }


        async function sendFont(sendData) {
            const url = font_save_url;
            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(sendData)
            });

            if (response.ok) {
                console.log("Success PUT ".concat(url));
                console.log(await response.json());
                window.alert("Success downloaded: " + sendData.title);
                
            }
            else {
                console.error("Error PUT: ".concat(url));
                console.error(await response.json());
            }
        }

        async function getTagCategories() {
            const url = tag_categories_get_url;
            try {
                const response = await fetch(url, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });

                if (response.ok) {
                    console.log("Success GET: " + url);
                    const data = await response.json();
                    return data;
                } else {
                    console.error("Error GET: " + url);
                }
            } catch (error) {
                console.error("Error:", error);
                console.error(response.json());
            }
        }



        function gettagCategoryTags() {
            const tagCategoryTags = {
                Numbers: [
                    "afrc",
                    "dnom",
                    "dtls",
                    "frac",
                    "numr",
                    "zero"
                ],
                Letterforms: [
                    "abvm",
                    "abvs",
                    "akhn",
                    "blwf",
                    "blws",
                    "blwm",
                    "calt",
                    "case",
                    "ccmp",
                    "cfar",
                    "cjct",
                    "clig",
                    "cpct",
                    "cswh",
                    "c2pc",
                    "c2sc",
                    "dist",
                    "dlig",
                    "expt",
                    "falt",
                    "fin2",
                    "fin3",
                    "fina",
                    "flac",
                    "half",
                    "haln",
                    "hist",
                    "hkna",
                    "hlig",
                    "hngl",
                    "hojo",
                    "init",
                    "isol",
                    "ital",
                    "jalt",
                    "jp78",
                    "jp83",
                    "jp90",
                    "jp04",
                    "liga",
                    "ljmo",
                    "locl",
                    "ltra",
                    "ltrm",
                    "med2",
                    "medi",
                    "mgrk",
                    "nalt",
                    "nlck",
                    "nukt",
                    "onum",
                    "ordn",
                    "ornm",
                    "pcap",
                    "pref",
                    "pres",
                    "pstf",
                    "psts",
                    "rand",
                    "rclt",
                    "rkrf",
                    "rlig",
                    "rtla",
                    "rtlm",
                    "ruby",
                    "salt",
                    "sinf",
                    "smpl",
                    "ssty",
                    "swsh",
                    "titl",
                    "tjmo",
                    "tnam",
                    "trad",
                    "unic",
                    "vert",
                    "vkna"
                ],
                GlyphWidth: [
                    "fwid",
                    "hwid",
                    "halt",
                    "pkna",
                    "pnum",
                    "pwid",
                    "qwid",
                    "stch",
                    "tnum",
                    "twid",
                    "vrt2",
                    "vrtr"
                ],
                CharacterVariants: [
                    "cv01-cv99"
                ],
                StylisticSets: [
                    "ss01-ss99"
                ],
                Spacing: [
                    "cpsp",
                    "chws",
                    "kern",
                    "palt",
                    "vchw",
                    "vhal",
                    "vkrn",
                    "vpal"
                ],
                Positioning: [
                    "curs",
                    "lfbd",
                    "lnum",
                    "mark",
                    "mkmk",
                    "rtbd",
                    "subs",
                    "sups",
                    "valt"
                ]
            };
            return tagCategoryTags;
        }