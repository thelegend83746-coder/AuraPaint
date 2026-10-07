package com.aurapaint.studio.brush

object BrushRegistry {
    val defaultPresets: List<BrushPreset> = listOf(
        // Pencils
        BrushPreset(
            id = "pencil_hb",
            name = "HB Drafting Pencil",
            category = BrushCategory.PENCIL,
            properties = BrushProperties(
                size = 12f,
                opacity = 0.85f,
                flow = 0.7f,
                spacing = 0.08f,
                hardness = 0.6f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 2.0f,
                grain = 0.35f
            ),
            description = "Natural graphite texture with fine touch response"
        ),
        BrushPreset(
            id = "pencil_6b",
            name = "6B Soft Graphite",
            category = BrushCategory.PENCIL,
            properties = BrushProperties(
                size = 28f,
                opacity = 0.95f,
                flow = 0.85f,
                spacing = 0.1f,
                hardness = 0.4f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 1.5f,
                grain = 0.5f
            ),
            description = "Rich dark shading pencil for expressive sketching"
        ),
        BrushPreset(
            id = "pencil_mechanical",
            name = "0.5mm Mechanical Pencil",
            category = BrushCategory.PENCIL,
            properties = BrushProperties(
                size = 4f,
                opacity = 0.95f,
                flow = 0.9f,
                spacing = 0.05f,
                hardness = 0.9f,
                pressureSize = false,
                pressureOpacity = true,
                stabilization = 4.0f
            ),
            description = "Precision sharp constant-width line drafting"
        ),

        // Inking
        BrushPreset(
            id = "ink_gpen",
            name = "Manga G-Pen",
            category = BrushCategory.INKING,
            properties = BrushProperties(
                size = 18f,
                opacity = 1.0f,
                flow = 1.0f,
                spacing = 0.05f,
                hardness = 0.98f,
                pressureSize = true,
                pressureOpacity = false,
                pressureSizeAmount = 1.2f,
                stabilization = 4.5f
            ),
            isFavorite = true,
            description = "Crisp, dynamic line-weight manga inking pen"
        ),
        BrushPreset(
            id = "ink_studiopen",
            name = "Studio Line Inker",
            category = BrushCategory.INKING,
            properties = BrushProperties(
                size = 14f,
                opacity = 1.0f,
                flow = 1.0f,
                spacing = 0.04f,
                hardness = 0.95f,
                pressureSize = true,
                pressureOpacity = false,
                stabilization = 6.0f
            ),
            isFavorite = true,
            description = "Silky stabilized ink for clean outline artwork"
        ),
        BrushPreset(
            id = "ink_technical",
            name = "Technical Fineliner",
            category = BrushCategory.INKING,
            properties = BrushProperties(
                size = 6f,
                opacity = 1.0f,
                flow = 1.0f,
                spacing = 0.05f,
                hardness = 1.0f,
                pressureSize = false,
                pressureOpacity = false,
                stabilization = 3.0f
            ),
            description = "Consistent uniform technical drafting line"
        ),

        // Painting
        BrushPreset(
            id = "paint_oil",
            name = "Wet Oil Brush",
            category = BrushCategory.PAINTING,
            properties = BrushProperties(
                size = 45f,
                opacity = 0.88f,
                flow = 0.65f,
                spacing = 0.12f,
                hardness = 0.75f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 2.0f
            ),
            isFavorite = true,
            description = "Heavy body impasto oil with rich blendable strokes"
        ),
        BrushPreset(
            id = "paint_watercolor",
            name = "Watercolor Wash",
            category = BrushCategory.PAINTING,
            properties = BrushProperties(
                size = 60f,
                opacity = 0.45f,
                flow = 0.35f,
                spacing = 0.18f,
                hardness = 0.25f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 2.0f,
                grain = 0.4f
            ),
            description = "Soft translucent layering watercolor glaze"
        ),
        BrushPreset(
            id = "paint_acrylic",
            name = "Flat Acrylic Bristle",
            category = BrushCategory.PAINTING,
            properties = BrushProperties(
                size = 35f,
                opacity = 0.95f,
                flow = 0.8f,
                spacing = 0.08f,
                hardness = 0.85f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 2.5f
            ),
            description = "Opaque textured brush with visible bristle direction"
        ),

        // Airbrush
        BrushPreset(
            id = "airbrush_soft",
            name = "Soft Airbrush",
            category = BrushCategory.AIRBRUSH,
            properties = BrushProperties(
                size = 80f,
                opacity = 0.5f,
                flow = 0.3f,
                spacing = 0.1f,
                hardness = 0.05f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 1.0f
            ),
            isFavorite = true,
            description = "Ultra smooth radial spray gradient"
        ),
        BrushPreset(
            id = "airbrush_hard",
            name = "Hard Edge Airbrush",
            category = BrushCategory.AIRBRUSH,
            properties = BrushProperties(
                size = 50f,
                opacity = 0.75f,
                flow = 0.5f,
                spacing = 0.08f,
                hardness = 0.45f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 1.0f
            ),
            description = "Controlled gradient with defined center falloff"
        ),

        // Sketch & Charcoal
        BrushPreset(
            id = "charcoal_vine",
            name = "Vine Charcoal",
            category = BrushCategory.SKETCH_CHARCOAL,
            properties = BrushProperties(
                size = 40f,
                opacity = 0.7f,
                flow = 0.5f,
                spacing = 0.15f,
                hardness = 0.3f,
                pressureSize = true,
                pressureOpacity = true,
                scatter = 15f,
                grain = 0.6f
            ),
            description = "Dusty, granular traditional charcoal stick"
        ),
        BrushPreset(
            id = "sketch_pastel",
            name = "Chalk Pastel",
            category = BrushCategory.SKETCH_CHARCOAL,
            properties = BrushProperties(
                size = 32f,
                opacity = 0.85f,
                flow = 0.7f,
                spacing = 0.12f,
                hardness = 0.55f,
                pressureSize = true,
                pressureOpacity = true,
                scatter = 8f,
                grain = 0.45f
            ),
            description = "Vibrant velvet chalk for expressive color blends"
        ),

        // Special FX
        BrushPreset(
            id = "fx_screentone",
            name = "Manga Screentone Dot",
            category = BrushCategory.SPECIAL_FX,
            properties = BrushProperties(
                size = 48f,
                opacity = 1.0f,
                flow = 1.0f,
                spacing = 0.25f,
                hardness = 0.9f,
                scatter = 12f,
                grain = 0.8f
            ),
            description = "Patterned halftone texture for manga shading"
        ),
        BrushPreset(
            id = "fx_glow",
            name = "Neon Light Glow",
            category = BrushCategory.SPECIAL_FX,
            properties = BrushProperties(
                size = 55f,
                opacity = 0.9f,
                flow = 0.6f,
                spacing = 0.08f,
                hardness = 0.2f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 3.0f
            ),
            description = "Vibrant intense luminous stroke with outer halo"
        ),

        // Erasers
        BrushPreset(
            id = "eraser_hard",
            name = "Hard Precision Eraser",
            category = BrushCategory.ERASER,
            properties = BrushProperties(
                size = 20f,
                opacity = 1.0f,
                flow = 1.0f,
                spacing = 0.05f,
                hardness = 0.98f,
                pressureSize = true,
                pressureOpacity = false,
                stabilization = 3.0f
            ),
            description = "Clean sharp edge eraser for crisp cutouts"
        ),
        BrushPreset(
            id = "eraser_kneaded",
            name = "Kneaded Soft Eraser",
            category = BrushCategory.ERASER,
            properties = BrushProperties(
                size = 45f,
                opacity = 0.4f,
                flow = 0.3f,
                spacing = 0.12f,
                hardness = 0.2f,
                pressureSize = true,
                pressureOpacity = true,
                stabilization = 1.5f
            ),
            description = "Gentle lifting eraser for highlights and soft fades"
        )
    )
}
