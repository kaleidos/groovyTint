package net.kaleidos.groovytint

import net.kaleidos.groovytint.ImageProcessor

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
