# PDFreader-WSO2ESB
How to install.

1. Install WSO2EI 6.1.1

2. Copy jar files from https://github.com/rswijesena/pdfreader/tree/master/libs to the <WSO2carbon_home>/libs folder.

3. Install https://github.com/rswijesena/pdfreader/blob/master/SAICompositeApplication_1.0.0.car car file.

4. Add folder location as a configuration file  /_system/governance/saiconfigs/filelocation to the wso2carbon registry.
For example - /Users/roshanwijesena/Documents/sai/files

5. Invoke the API. http://192.168.1.5:8280/readfiles/filename/{filename}/type/{fileType} 

For example, http://192.168.1.5:8280/readfiles/filename/textonly/type/zip

API source - https://github.com/rswijesena/pdfreader/blob/master/SAI/src/main/synapse-config/api/PDFReader.xml
Class mediator - https://github.com/rswijesena/pdfreader/blob/master/classmediator/src/main/java/com/sai/pdf/PdfReader.java

If your file is a ZIP file it will automatically extract to the folder location and read the content of the PDF and send the response as post body.
![alt text](https://github.com/rswijesena/pdfreader/blob/master/api-sample.png)
