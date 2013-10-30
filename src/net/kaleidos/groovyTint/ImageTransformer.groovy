package net.kaleidos.groovyTint

import net.kaleidos.groovyTint.ImageProcessor

class ImageTransformer {
    ImageProcessor processor

    ImageTransformer() {
        processor = new ImageProcessor()
    }

    ImageTransformer(ImageProcessor customProcessor) {
        processor = customProcessor
    }

    def transform(image, actions) {
        def img = image
        actions.each { action ->
            img = processor."${action.action}"(img, action)
        }
        return img
    }
}
