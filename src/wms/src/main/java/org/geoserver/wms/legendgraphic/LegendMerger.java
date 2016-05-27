/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.wms.legendgraphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.legendgraphic.LegendUtils.LegendLayout;
import org.geoserver.wms.map.ImageUtils;
import org.geotools.styling.Description;
import org.geotools.styling.Rule;
import org.opengis.util.InternationalString;

public class LegendMerger {
    
    /**
     * Receives a list of <code>BufferedImages</code> and produces a new one which holds all the images in <code>imageStack</code> one above the
     * other.
     * 
     * @param imageStack the list of BufferedImages
     * @param dx horizontal space between images
     * @param dy vertical space between images
     * @param margin padding around image
     * @param transparent if true make legend transparent
     * @param backgroundColor background color of legend
     * @param antialias if true applies anti aliasing
     * @param layout orientation of legend, my be horizontal or vertical
     * @param maxWidth maximum width for horizontal legend
     * @param rows maximum number of rows for horizontal legend
     * @param maxHeight maximum height for vertical legend
     * @param columns maximum number of columns for vertical legend
     * @return the legend image with all the images on the argument list.
     */
    public static BufferedImage mergeLegends(List<BufferedImage> imageStack, int dx, int dy,
            int margin, Color backgroundColor, boolean transparent, boolean antialias,
            LegendLayout layout, int maxWidth, int rows, int maxHeight, int columns) {

        // Disposes legend nodes into a matrix according to layout rules
        BufferedImage[][] legendMatrix = null;

        if (layout == LegendLayout.HORIZONTAL) {
            legendMatrix = createHorizontalLayoutMatrix(imageStack, maxWidth, rows);
        }

        if (layout == LegendLayout.VERTICAL) {
            legendMatrix = createVerticalLayoutMatrix(imageStack, maxHeight, columns);
        }

        // Computes total width and height and limits they according to layout rules
        int totalHeight = 0;
        int totalWidth = 0;
        int rowNumber = 0;
        int columnNumber = 0;
        if(legendMatrix.length > 0){
            columnNumber = legendMatrix[0].length;
        }
        int[] colsWidth = new int[columnNumber];
        for (rowNumber = 0; rowNumber < legendMatrix.length; rowNumber++) {
            BufferedImage[] row = legendMatrix[rowNumber];
            int rowHeigth = 0;
            for (columnNumber = 0; columnNumber < row.length; columnNumber++) {
                BufferedImage node = legendMatrix[rowNumber][columnNumber];
                if (node != null) {
                    colsWidth[columnNumber] = Math.max( colsWidth[columnNumber], node.getWidth());
                    rowHeigth = Math.max(rowHeigth, node.getHeight());
                }
            }
            totalHeight = totalHeight + rowHeigth;
        }
        
        for(int w : colsWidth){
            totalWidth = totalWidth + w;
        }        

        if (layout == LegendLayout.VERTICAL && maxHeight > 0 && totalHeight > maxHeight) {
            totalHeight = maxHeight;
        }
        if (layout == LegendLayout.HORIZONTAL && maxWidth > 0 && totalWidth > maxWidth) {
            totalWidth = maxWidth;
        }

        // Apply margin corrections
        totalHeight = totalHeight + (int) (rowNumber * dy) + (int) (2 * margin);
        totalWidth = totalWidth + (int) (columnNumber * dx) + (int) (2 * margin);

        // fix 0 size
        if (totalHeight == 0)
            totalHeight = 1;
        if (totalWidth == 0)
            totalWidth = 1;

        // Build final image
        final BufferedImage finalLegend = buildFinalLegend(dx, dy, margin, totalHeight, totalWidth, colsWidth,
                transparent, backgroundColor, antialias, legendMatrix);

        return finalLegend;
    }

    /**
     * Receives a list of <code>BufferedImages</code> and produces a new one
     * which holds all the images in <code>imageStack</code> one above the
     * other, handling labels.
     * 
     * @param imageStack
     *            the list of BufferedImages, one for each applicable Rule
     * @param rules
     *            The applicable rules, one for each image in the stack (if not
     *            null it's used to compute labels)
     * @param request
     *            The request.
     * @param forceLabelsOn
     *            true for force labels on also with a single image.
     * @param forceLabelsOff
     *            true for force labels off also with more than one rule.
     * 
     * @return the image with all the images on the argument list.
     * 
     */
    public static BufferedImage mergeLegends(List<RenderedImage> imageStack, Rule[] rules, GetLegendGraphicRequest req,
            boolean forceLabelsOn, boolean forceLabelsOff) {

        final boolean transparent = req.isTransparent();
        final Color backgroundColor = LegendUtils.getBackgroundColor(req);
        Font labelFont = LegendUtils.getLabelFont(req);
        boolean useAA = LegendUtils.isFontAntiAliasing(req);
        int buffer = 2;

        // Builds legend nodes (graphics + label)
        final int imgCount = imageStack.size();
        List<BufferedImage> nodes = new ArrayList<BufferedImage>();
        //Single legend, no rules, no force label
        if (imgCount == 1 && (!forceLabelsOn || rules == null)) {
            return (BufferedImage) imageStack.get(0);
        }else{
            for (int i = 0; i < imgCount; i++) {
                BufferedImage img = (BufferedImage) imageStack.get(i);
                if(rules != null && rules[i] != null){
                    BufferedImage label = renderLabel(img, rules[i], req, forceLabelsOff);
                    if(label != null){
                        img = joinBufferedImage(img, label, labelFont, useAA, transparent, backgroundColor);
                    }
                    nodes.add(img);
                }else{
                    nodes.add(img);
                }
            }
        }

        // Sets legend nodes into a matrix according to layout rules
        BufferedImage[][] legendMatrix = null;
        LegendLayout layout = req.getLayout();

        if (layout == LegendLayout.HORIZONTAL) {
            legendMatrix = createHorizontalLayoutMatrix(nodes,  req.getRowWidth(), req.getRows());
        }

        if (layout == LegendLayout.VERTICAL) {
            legendMatrix = createVerticalLayoutMatrix(nodes,  req.getColumnHeight(), req.getColumns());
        }

        // Computes total width and height and limits they according to layout rules
        int totalHeight = 0;
        int totalWidth = 0;
        int rowNumber = 0;
        int columnNumber = 0;
        if(legendMatrix.length > 0){
            columnNumber = legendMatrix[0].length;
        }
        int[] colsWidth = new int[columnNumber];
        for (rowNumber = 0; rowNumber < legendMatrix.length; rowNumber++) {
            BufferedImage[] row = legendMatrix[rowNumber];
            int rowHeigth = 0;
            for (columnNumber = 0; columnNumber < row.length; columnNumber++) {
                BufferedImage node = legendMatrix[rowNumber][columnNumber];
                if (node != null) {
                    colsWidth[columnNumber] = Math.max( colsWidth[columnNumber], node.getWidth());
                    rowHeigth = Math.max(rowHeigth, node.getHeight());
                }
            }
            totalHeight = totalHeight + rowHeigth;
        }
        
        for(int w : colsWidth){
            totalWidth = totalWidth + w;
        }        

        // buffer the width a bit
        totalWidth += buffer;

        int maxHeight = req.getColumnHeight();
        int maxWidth = req.getRowWidth();
        if (layout == LegendLayout.VERTICAL && maxHeight > 0 && totalHeight > maxHeight) {
            totalHeight = maxHeight;
        }
        if (layout == LegendLayout.HORIZONTAL && maxWidth > 0 && totalWidth > maxWidth) {
            totalWidth = maxWidth;
        }

        //fix 0 size
        if(totalHeight == 0) totalHeight = 1;
        if(totalWidth == 0) totalWidth = 1;

        // Build final image
        final BufferedImage finalLegend = buildFinalLegend(0, 0, 0, totalHeight, totalWidth, colsWidth, transparent, backgroundColor, useAA, legendMatrix);

        return finalLegend;

    }

    private static BufferedImage[][] createVerticalLayoutMatrix(List<BufferedImage> nodes, int maxHeight,
            int maxColumns) {
        BufferedImage[][] legendMatrix = new BufferedImage[0][0];
        /*
         * Limit max height
         */
        if (maxHeight > 0) {
            /*
             * Limit max column
             */
            int cnLimit = maxColumns > 0 ? maxColumns : nodes.size();
            BufferedImage[][] maxLegendMatrix = new BufferedImage[nodes.size()][cnLimit];

            int maxCn = 0;
            int maxRn = 0;
            int cn = 0;
            int rn = 0; 
            int columnHeight = 0;
            for (int i = 0; i < nodes.size(); i++) {               
                BufferedImage node = nodes.get(i);
                columnHeight = columnHeight + node.getHeight();
                if (columnHeight <= maxHeight) {
                    //At first row increase total column counter
                    if(rn == 0){
                        maxCn++;
                    }
                    //Fill current column
                    maxLegendMatrix[rn][cn] = node;
                    //Increase row counter
                    rn++;
                    //Update total row counter
                    maxRn = Math.max(maxRn, rn);
                } else {
                    //Add current node to next column
                    i--;
                    cn++;
                    //Stop if column limits is reached
                    if (cn == cnLimit) {
                        break;
                    }
                    //Reset column counter
                    columnHeight = 0;
                    rn = 0;
                }
            }
            /*
             * Resize matrix to exact sizes
             */
            legendMatrix = new BufferedImage[maxRn][maxCn];
            for (int i = 0; i < maxRn; i++) {
                System.arraycopy(maxLegendMatrix[i], 0, legendMatrix[i], 0, maxCn);
            }
        } else {
            /*
             * Limit max column, if no limit set it to 1
             */
            int colNumber = maxColumns > 0 ? maxColumns : 1;
            int rowNumber = (int) Math.ceil((float) nodes.size() / colNumber);
            legendMatrix = new BufferedImage[rowNumber][colNumber];
            for (int i = 0; i < nodes.size(); i++) {
                int cn = i / rowNumber;
                int rn = i - (cn * rowNumber);
                legendMatrix[rn][cn] = nodes.get(i);
            }
        }

        return legendMatrix;
    }

    private static BufferedImage[][] createHorizontalLayoutMatrix(List<BufferedImage> nodes, int maxWidth,
            int maxRows) {
        BufferedImage[][] legendMatrix = new BufferedImage[0][0];
        /*
         * Limit max height
         */
        if (maxWidth > 0) {
            /*
             * Limit max column
             */
            int rwLimit = maxRows > 0 ? maxRows : nodes.size();
            BufferedImage[][] maxLegendMatrix = new BufferedImage[rwLimit][nodes.size()];

            int maxCn = 0;
            int maxRn = 0;
            int cn = 0;
            int rn = 0; 
            int rowWidth = 0;
            for (int i = 0; i < nodes.size(); i++) {               
                BufferedImage node = nodes.get(i);
                rowWidth = rowWidth + node.getWidth();
                if (rowWidth <= maxWidth) {
                    //At first column increase total row counter
                    if(cn == 0){
                        maxRn++;
                    }
                    //Fill current column
                    maxLegendMatrix[rn][cn] = node;
                    //Increase row counter
                    cn++;
                    //Update total column counter
                    maxCn = Math.max(maxCn, cn);
                } else {
                    //Add current node to next row
                    i--;
                    rn++;
                    //Stop if row limits is reached
                    if (rn == rwLimit) {
                        break;
                    }
                    //Reset column counter
                    rowWidth = 0;
                    cn = 0;
                }
            }
            /*
             * Resize matrix to exact sizes
             */
            legendMatrix = new BufferedImage[maxRn][maxCn];
            for (int i = 0; i < maxRn; i++) {
                System.arraycopy(maxLegendMatrix[i], 0, legendMatrix[i], 0, maxCn);
            }
        } else {
            /*
             * Limit max row, if no limit set it to 1
             */

            int rowNumber = maxRows > 0 ? maxRows : 1;
            int colNumber = (int) Math.ceil((float) nodes.size() / rowNumber);
            legendMatrix = new BufferedImage[rowNumber][colNumber];
            for (int i = 0; i < nodes.size(); i++) {
                int rn = i / colNumber;
                int cn = i - (rn * colNumber);
                legendMatrix[rn][cn] = nodes.get(i);
            }
        }

        return legendMatrix;
    }

    /**
     * Renders legend matrix and cut off the node that exceeds the maximum
     * limits
     * 
     * @param totalHeight
     *            maximum height of legend
     * @param totalWidth
     *            maximum width of legend
     * @param colsWidth 
     * @param transparent
     *            if true make legend transparent
     * @param backgroundColor
     *            background color of legend
     * @param useAA
     *            if true applies anti aliasing
     * @param legendMatrix
     *            the matrix of nodes of legend
     * @return BufferedImage of legend
     * 
     */
    private static BufferedImage buildFinalLegend(int dx, int dy, int margin, int totalHeight, int totalWidth, int[] colsWidth, boolean transparent, Color backgroundColor,
            boolean useAA, BufferedImage[][] legendMatrix) {
        final BufferedImage finalLegend = ImageUtils.createImage(totalWidth, totalHeight, (IndexColorModel) null,
                transparent);
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<RenderingHints.Key, Object>();
        Graphics2D finalGraphics = ImageUtils.prepareTransparency(transparent, backgroundColor, finalLegend, hintsMap);
        //finalGraphics.setFont(labelFont);
        if (useAA) {
            finalGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            finalGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        int vOffset = margin;
        for (int rowNumber = 0; rowNumber < legendMatrix.length; rowNumber++) {
            BufferedImage[] row = legendMatrix[rowNumber];
            int rowH = 0;
            int hOffset = margin;
            for (int columnNumber = 0; columnNumber < row.length; columnNumber++) {
                BufferedImage node = legendMatrix[rowNumber][columnNumber];
                if (node != null) {
                    int maxW = hOffset + colsWidth[columnNumber];
                    int maxH = vOffset + node.getHeight();
                    if (maxW <= totalWidth && maxH <= totalHeight) {
                        finalGraphics.drawImage(node, hOffset, vOffset, null);
                        hOffset = hOffset + colsWidth[columnNumber] + dx;
                        rowH = Math.max(rowH, node.getHeight());
                    } else {
                        break;
                    }
                }
            }
            vOffset = vOffset + rowH +dy;
        }

        finalGraphics.dispose();

        return finalLegend;
    }

    /**
     * Join image and label to create a single legend node image
     * 
     * @param img
     *            image of legend
     * @param label
     *            label of legend
     * @param labelFont
     *            font to use
     * @param useAA
     *            if true applies anti aliasing
     * @param transparent
     *            if true make legend transparent
     * @param backgroundColor
     *            background color of legend
     * @return BufferedImage of image and label side by side and vertically
     *         center
     */
    private static BufferedImage joinBufferedImage(BufferedImage img, BufferedImage label, Font labelFont, boolean useAA,
            boolean transparent, Color backgroundColor) {
        // do some calculate first
        int offset = 0;
        int wid = img.getWidth() + label.getWidth() + offset;
        int height = Math.max(img.getHeight(), label.getHeight()) + offset;
        // create a new buffer and draw two image into the new image
        BufferedImage newImage = ImageUtils.createImage(wid, height, (IndexColorModel) null, transparent);
        final Map<RenderingHints.Key, Object> hintsMap = new HashMap<RenderingHints.Key, Object>();
        Graphics2D g2 = ImageUtils.prepareTransparency(transparent, backgroundColor, newImage, hintsMap);
        g2.setFont(labelFont);
        if (useAA) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        // move the images to the vertical center of the row
        int imgOffset = (int) Math.round((height - img.getHeight()) / 2d);
        int labelOffset = (int) Math.round((height - label.getHeight()) / 2d);
        g2.drawImage(img, null, 0, imgOffset);
        g2.drawImage(label, null, img.getWidth() + offset, labelOffset);
        g2.dispose();
        return newImage;
    }

    /**
     * Renders the legend image label
     * 
     * @param img
     *            the BufferedImage
     * @param rule
     *            the applicable rule for img, if rule is not null the label will be rendered
     * @param req
     *            the request
     * @param forceLabelsOff
     *            true for force labels off also with more than one rule
     * @return the BufferedImage of label
     * 
     */
    private static BufferedImage renderLabel(RenderedImage img, Rule rule, GetLegendGraphicRequest req,
            boolean forceLabelsOff) {
        BufferedImage labelImg = null;
        if (!forceLabelsOff && rule != null) {
            // What's the label on this rule? We prefer to use
            // the 'title' if it's available, but fall-back to 'name'
            final Description description = rule.getDescription();
            Locale locale = req.getLocale();
            String label = "";
            if (description != null && description.getTitle() != null) {
                final InternationalString title = description.getTitle();
                if (locale != null) {
                    label = title.toString(locale);
                } else {
                    label = title.toString();
                }
            } else if (rule.getName() != null) {
                label = rule.getName();
            }
            if (label != null && label.length() > 0) {
                final BufferedImage renderedLabel = getRenderedLabel((BufferedImage) img, label, req);
                labelImg = renderedLabel;
            }
        }
        return labelImg;
    }

    /**
     * Renders a label on the given image, using parameters from the request
     * for the rendering style.
     * 
     * @param image
     * @param label
     * @param request
     *
     */
    protected static BufferedImage getRenderedLabel(BufferedImage image, String label,
            GetLegendGraphicRequest request) {
        Font labelFont = LegendUtils.getLabelFont(request);
        boolean useAA = LegendUtils.isFontAntiAliasing(request);

        final Graphics2D graphics = image.createGraphics();
        graphics.setFont(labelFont);
        if(useAA) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        return LegendUtils.renderLabel(label, graphics, request);
    }



}
