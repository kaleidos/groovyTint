GroovyTint
----------

groovyTint command
~~~~~~~~~~~~~~~~~~

You can use groovyTint command to transform any image with a list of actions extracted from a catalog of transformations (written in jsom).

Usage example::

  groovyTint input-image.png transformations.json transformation1 output-image.png

And the transformations.json can be something like this:

.. code-block:: json
  {
    "transofrmation1": [
      {
        "action": 'fit',
        "width": 1024,
        "height": 768,
        "align": 'center',
        "valign": 'middle',
      },
      {
        "action": 'trim'
      },
      {
        "action": 'equalize'
      }
    ]
  }


Default ImageProcessor Actions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GroovyTint default ImageProcessor comes with some actions, here you have the
list:

+-----------------+-------------------------------+--------------------------+
| Action          | Description                   | Params                   |
+=================+===============================+==========================+
| crop            | Crop an image to a width and  | height, width, align,    |
|                 | height.                       | valign                   |
+-----------------+-------------------------------+--------------------------+
| circularCrop    | Crop a cirlce of an image to  | height, width, align,    |
|                 | a width and height.           | valign                   |
+-----------------+-------------------------------+--------------------------+
| scale           | Scale an image to a width and | height, width            |
|                 | height (deforming it).        |                          |
+-----------------+-------------------------------+--------------------------+
| fit             | Scale an image to a width and | height, width, align,    |
|                 | height and crop the overflow. | valign                   |
+-----------------+-------------------------------+--------------------------+
| grayscale       | Convert the image to          |                          |
|                 | grayscale.                    |                          |
+-----------------+-------------------------------+--------------------------+
| flip            | Flip the image vertically.    |                          |
+-----------------+-------------------------------+--------------------------+
| mirror          | Flip the image horizontally.  |                          |
+-----------------+-------------------------------+--------------------------+
| equalize        | Equalize the image histogram. |                          |
+-----------------+-------------------------------+--------------------------+
| autocontrast    | Maximize (normalize) image    | cutoff                   |
|                 | contrast.                     |                          |
+-----------------+-------------------------------+--------------------------+
| invert          | Invert the image colors.      |                          |
+-----------------+-------------------------------+--------------------------+
| trim            | Remove equal color border.    |                          |
+-----------------+-------------------------------+--------------------------+
| scale           | Scale the image to a with and | height, width            |
|                 | height.                       |                          |
+-----------------+-------------------------------+--------------------------+
| rotate          | Rotate an image a number of   | degrees                  |
|                 | degrees.                      |                          |
+-----------------+-------------------------------+--------------------------+
| cornerPin       | Deform the image moving the   | left_top, left_bottom,   |
|                 | corners to a position.        | right_top, right_bottom  |
+-----------------+-------------------------------+--------------------------+

Transformations Object
~~~~~~~~~~~~~~~~~~~~~~

Example:

.. code-block:: json
  [
    {
      "action": 'fit',
      "width": 1024,
      "height": 768,
      "align": 'center',
      "valign": 'middle',
    },
    {
      "action": 'trim'
    },
    {
      "action": 'equalize'
    }
  ]
