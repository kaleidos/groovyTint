#!/usr/bin/env groovy

System.setProperty("jmagick.systemclassloader","false")
import magick.ImageInfo
import magick.MagickImage
import groovy.json.JsonSlurper
import net.kaleidos.groovytint.ImageTransformer

if(this.args.length != 4){
    println "Usage: groovyTint <image_path> <json_path> <process_name> <output_path>"
    return
}

def slurper = new JsonSlurper()
def processes = slurper.parseText(new File(this.args[1]).text)

ImageInfo ii = new ImageInfo(this.args[0])
MagickImage mi = new MagickImage(ii)
transformer = new ImageTransformer()
mi = transformer.transform(mi, processes."${this.args[2]}")
mi.setFileName(this.args[3])
mi.writeImage(ii)
