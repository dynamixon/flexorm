package trans.datazilla

import org.apache.commons.io.FileUtils

class DzTrans{
    static void main(String[] args) {

        String flexormBaseDir = ((args!=null&&args.length>0)?args[0]:'./')
        String datazillaOutputBase = ((args!=null&&args.length>1)?args[1]:'/dz_trans/')
        String datazillaOutputDir = datazillaOutputBase+'/datazilla'
        def datazillaOutputPath = new File(datazillaOutputDir)
        FileUtils.deleteQuietly(datazillaOutputPath)
        FileUtils.forceMkdir(datazillaOutputPath)

        dealRootFiles(flexormBaseDir,datazillaOutputDir)
        dealSrcPkg(flexormBaseDir,datazillaOutputDir)
        dealSrcRes(flexormBaseDir,datazillaOutputDir)
        replaceContent(new File(datazillaOutputDir),commonReplaceMap())
    }

    static void dealRootFiles(String flexormBaseDir,String datazillaOutputDir){
        File flexormBasePath = new File(flexormBaseDir)
        List<String> ignoredFileAndPath = ['.git','.idea','target']
        def rootFiles = flexormBasePath.listFiles(new FileFilter() {
            @Override
            boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false
                }
                return !ignoredFileAndPath.contains(pathname.getName())
            }
        })
        rootFiles.each {
            String targetFileName = datazillaOutputDir+'/'+it.name
            def targetFile = new File(targetFileName)
            FileUtils.copyFile(it, targetFile)
        }
    }

    static void dealSrcPkg(String flexormBaseDir,String datazillaOutputDir){
        Map<String,String> srcPkgCopyToMap = [
            'src/main/java/io/github/dynamixon/flexorm':'src/main/java/com/github/haflife3/datazilla',
            'src/test/java/io/github/dynamixon':'src/test/java/com/github/haflife3'
        ]
        srcPkgCopyToMap.each {
            File flexormSrcPkgPath = new File(flexormBaseDir+'/'+it.key)
            File datazillaSrcPkgPath = new File(datazillaOutputDir+'/'+it.value)
            FileUtils.copyDirectory(flexormSrcPkgPath,datazillaSrcPkgPath)
        }
    }

    static void dealSrcRes(String flexormBaseDir,String datazillaOutputDir){
        List<String> srcResList = ['src/main/resources','src/test/resources']
        srcResList.each {
            File flexormSrcResPath = new File(flexormBaseDir+'/'+it)
            File datazillaSrcResPath = new File(datazillaOutputDir+'/'+it)
            FileUtils.copyDirectory(flexormSrcResPath,datazillaSrcResPath)
        }
        srcResList.each {
            File metaInfServicesPath = new File(datazillaOutputDir+'/'+it+'/META-INF/services')
            metaInfServicesPath.listFiles().each {
                def origName = it.name
                if(origName.contains('io.github.dynamixon.flexorm')){
                    String newName = origName.replace('io.github.dynamixon.flexorm','com.github.haflife3.datazilla')
                    FileUtils.moveFile(it,new File(metaInfServicesPath.absolutePath+'/'+newName))
                }
            }
        }
    }

    static void replaceContent(File file, Map<String,String> replaceMap){
        if(file.isDirectory()){
            println "dir: $file.absolutePath ======= "
            def files = file.listFiles()
            files.each {
                replaceContent(it,replaceMap)
            }
        }else {
            def origContent = file.getText('utf-8')
            String newContent = origContent
            replaceMap.each {
                newContent = newContent.replace(it.key,it.value)
            }
            FileUtils.write(file,newContent, 'utf-8')
            println "file: $file.absolutePath finish +++++++ "
        }
    }

    static Map<String,String> commonReplaceMap(){
        return [
            //pom
                //version !!! CHANGE ACCORDINGLY !!!
            '<version>1.2.3-SNAPSHOT</version>':'<version>1.4.8-SNAPSHOT</version>',
            '<email>ktsny@163.com</email>':'<email>halflife3@163.com</email>',
            'github.com/dynamixon/flexorm.git':'github.com/halflife3/datazilla.git',
            'https://s01.oss.sonatype.org/':'https://oss.sonatype.org/',
            '<name>dynamixon</name>':'<name>maojianfeng</name>',

            //general
            'io.github.dynamixon.flexorm':'com.github.haflife3.datazilla',
            'io.github.dynamixon':'com.github.haflife3',
            'flexorm':'datazilla'
        ]
    }
}
