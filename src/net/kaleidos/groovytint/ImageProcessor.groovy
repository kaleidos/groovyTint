package net.kaleidos.groovytint

import magick.ImageInfo
import magick.MagickImage
import magick.ColorspaceType
import java.awt.Rectangle

class ImageProcessor {
    protected Rectangle getCropRectangle(image, params) {
        int left = 0
        int top = 0
        int originWidth = image.getDimension().width
        int originHeight = image.getDimension().height
        int dstWidth = params.width
        int dstHeight = params.height

        String align = params.align
        String valign = params.valign

        if (originWidth >= dstWidth) {
            switch(align) {
                case "center":
                    left = (originWidth/2)-(dstWidth/2)
                    break
                case "left":
                    left = 0
                    break
                case "right":
                    left = originWidth-dstWidth
                    break
                default:
                    throw Exception('Invalid align')
            }
        }

        if (originHeight >= dstHeight) {
            switch(valign) {
                case "middle":
                    top = (originHeight/2)-(dstHeight/2)
                    break
                case "top":
                    top = 0
                    break
                case "bottom":
                    top = originHeight-dstHeight
                    break
                default:
                    throw Exception('Invalid valign')
            }
        }
        return new Rectangle(left, top, dstWidth, dstHeight)
    }

    MagickImage crop(image, params) {
        return image.cropImage(getCropRectangle(image, params))
    }

    MagickImage circularCrop(image, params) {
        Rectangle rectangle = getCropRectangle(image, params)

        image = crop(image, params)

        MagickImage img = runCommand image, params, { src, dst ->

            return [
                "convert",
                src,
                "(",
                "+clone",
                "-threshold",
                "-1",
                "-negate",
                "-fill",
                "white",
                "-draw",
                "circle ${rectangle.width/2},${rectangle.height/2} ${rectangle.width/2},0",
                ")",
                "-alpha",
                "off",
                "-compose",
                "copy_opacity",
                "-composite",
                dst
            ]
        }
        return img
    }

    MagickImage fit(image, params) {
        int originWidth = image.dimension.width
        int originHeight = image.dimension.height
        int dstWidth = params.width
        int dstHeight = params.height

        if (originWidth / dstWidth <= originHeight / dstHeight) {
            image = image.scaleImage(dstWidth, (originHeight / (originWidth/dstWidth)).intValue())
        } else {
            image = image.scaleImage((originWidth / (originHeight/dstHeight)).intValue(), dstHeight)
        }
        return crop(image, params)
    }

    MagickImage grayscale(image, params) {
        image.rgbTransformImage(ColorspaceType.GRAYColorspace)
        return image
    }

    MagickImage flip(image, params) {
        return image.flipImage()
    }

    MagickImage mirror(image, params) {
        return image.flopImage()
    }

    MagickImage invert(image, params) {
        image.negateImage(0)
        return image
    }

    MagickImage autocontrast(image, params) {
        image.contrastImage(false)
        return image
    }

    MagickImage equalize(image, params) {
        image.equalizeImage()
        return image
    }

    MagickImage scale(image, params) {
        return image.scaleImage(params.width, params.height)
    }

    MagickImage rotate(image, params) {
        return image.rotateImage(params.degrees)
    }

    MagickImage trim(image, params) {
        return image.trimImage()
    }

    MagickImage cornerPin(image, params) {
        MagickImage img = runCommand image, params, { src, dst ->
            int width = image.dimension.width
            int height = image.dimension.height
            def corners = new StringBuilder()
            corners << "0,0,${params.left_top[0]},${params.left_top[1]}"
            corners << " 0,${height},${params.left_bottom[0]},${params.left_bottom[1]},"
            corners << " ${width},0,${params.right_top[0]},${params.right_top[1]}"
            corners << " ${width},${height},${params.right_bottom[0]},${params.right_bottom[1]}"
            return ["convert", src, "-virtual-pixel", "transparent", "-distort", "Perspective", "${corners.toString()}", dst]
        }
        return img
    }

    protected MagickImage runCommand(image, params, func) {
        File tempOrigin = File.createTempFile("temp",".cornerpin.png")
        File tempDst = File.createTempFile("temp",".cornerpinResult.png")

        ImageInfo ii = new ImageInfo(tempOrigin.absolutePath)
        image.setFileName(tempOrigin.absolutePath)
        image.writeImage(ii)

        def command = func(tempOrigin.absolutePath, tempDst.absolutePath)

        def proc = command.execute()
        proc.waitFor()

        ImageInfo iiDst = new ImageInfo(tempDst.absolutePath)
        image = new MagickImage(iiDst)

        return image
    }

    MagickImage drawText(image, params) {
        // TODO
        return image
    }

    MagickImage watermark(image, params) {
        // TODO
        return image
    }
}
