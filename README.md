# AuraPaint Studio

A production-quality native Android digital drawing, illustration, painting, and frame-by-frame animation studio built from scratch in Kotlin and Jetpack Compose.

---

## 🎨 Overview & Product Vision

**AuraPaint Studio** is designed as a serious mobile-first art application for Android. It combines the immediacy and compact touch-first interaction patterns of professional mobile drawing software with high-performance native canvas rendering, professional parametric brushes, non-destructive layer compositing, vector line-art, live tone curves, and frame-by-frame animation.

---

## 🦊 Original Mascot & Brand Identity: "Kuro"

- **Mascot Concept**: *Kuro*, the nocturnal ink-sprite.
- **Silhouette**: Minimalist deep charcoal rounded silhouette with a dynamic curved brush-tail, electric-blue (`#3D7BFF`) and neon violet (`#8A5CFF`) aura, and friendly artistic personality.
- **Assets**:
  - Master vector logo: `res/drawable/ic_aurapaint_logo.xml`
  - Adaptive launcher icons: `res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`
  - Launcher foreground & background vectors: `res/drawable/ic_launcher_foreground.xml` and `ic_launcher_background.xml`

---

## 🛠 Architecture & Tech Stack

```
com.aurapaint.studio/
├── core/
│   ├── AppConfig.kt            # Centralized constants, defaults & paths
│   ├── theme/                  # Dark graphite studio theme, typography & palette
│   ├── navigation/             # Type-safe screen navigation states
│   └── util/                   # MathUtils, ColorUtils, BitmapUtils
├── canvas/
│   ├── CanvasView.kt           # Custom high-performance hardware-accelerated View
│   ├── CanvasController.kt     # Coordinates input, active tool, layers, history
│   ├── ViewTransform.kt        # Multi-touch zoom, pan, rotation & coordinate mapping
│   ├── InputTracker.kt         # Distinguishes 1-finger draw, 2-finger navigation & stylus
│   └── CanvasState.kt          # Tool states & viewport parameters
├── brush/
│   ├── BrushProperties.kt      # Size, opacity, flow, spacing, hardness, stabilizer, scatter
│   ├── BrushPreset.kt          # Preset models & categories
│   ├── BrushRegistry.kt        # Curated library (Pencils, Pens, Oils, Acrylics, Airbrush)
│   ├── BrushEngine.kt          # High-performance dab interpolation & pressure scaling
│   └── Stabilizer.kt           # Weighted moving-average stroke stabilizer
├── layers/
│   ├── Layer.kt                # Base layer interface
│   ├── RasterLayer.kt          # Bitmap-backed layer with alpha-lock & thumbnails
│   ├── VectorLayer.kt          # Scalable vector paths & bezier rendering
│   ├── LayerCompositor.kt      # Composites blend modes & clipping masks
│   ├── LayerManager.kt         # Reorder, merge down, duplicate, delete, visibility
│   └── BlendMode.kt            # PorterDuff & custom blending modes
├── vector/
│   ├── VectorPoint.kt          # Control handles & coordinate nodes
│   ├── VectorPath.kt           # Path stroke, fill, and closed state
│   └── VectorRenderer.kt       # Bezier curve renderer
├── selection/
│   ├── SelectionMask.kt        # Marching ants overlay & clip boundaries
│   └── SelectionManager.kt     # Rectangle, Ellipse, Lasso, Invert, Clear
├── transform/
│   └── TransformManager.kt     # Interactive move, scale, rotate, flip H/V
├── shapes/
│   ├── ShapeTool.kt            # Line, Rect, Rounded Rect, Ellipse, Polygon, Star, Arrow
│   └── FloodFill.kt            # High-performance BFS flood fill with tolerance
├── symmetry/
│   └── SymmetryManager.kt      # Vertical, Horizontal, 4-Way, Radial (6/8 segments)
├── rulers/
│   └── RulerManager.kt         # Straight ruler, Circle guide, Perspective grid
├── color/
│   ├── ColorManager.kt         # Active/previous colors, recent history, eyedropper
│   └── PalettePresets.kt       # Essentials, Manga, Cyberpunk, Skin tones, Pastel
├── filters/
│   ├── FilterProcessor.kt      # Brightness, Contrast, Saturation, Blur, Sharpen, etc.
│   └── ToneCurve.kt            # Interactive 256-entry spline LUT curve editor
├── materials/
│   └── MaterialAsset.kt        # Screentones, cold-press paper, canvas weave, speedlines
├── text/
│   └── TextElement.kt          # Multiline typography, font size, bold, italic
├── reference/
│   └── ReferenceManager.kt     # Movable & resizable floating PIP reference window
├── animation/
│   ├── AnimationTimeline.kt    # Frame manager, FPS slider (1..30), play/pause
│   ├── OnionSkin.kt            # Color-tinted onion skinning (red/blue & cyan/green)
│   └── Frame.kt                # Frame model with live thumbnail caching
├── timelapse/
│   ├── CreativeAction.kt       # Process log data models
│   └── TimelapseRecorder.kt    # Action recorder & keyframe generator
├── history/
│   ├── Command.kt              # Action command interface & layer bitmap snapshots
│   └── UndoManager.kt          # Deep undo/redo stack
├── project/
│   ├── Project.kt              # Project metadata model
│   ├── PresetCanvas.kt         # Square, Portrait, Landscape, 4K, Social, Manga
│   ├── ProjectSerializer.kt    # JSON header + individual PNG layer persistence
│   └── ProjectRepository.kt    # Project listing, creation, duplication, deletion
├── export/
│   └── ArtworkExporter.kt      # High-res export to PNG, Transparent PNG, JPEG
└── ui/
    ├── home/                   # HomeScreen & NewArtworkDialog
    ├── editor/                 # EditorScreen, TopEditorBar, BottomEditorBar, QuickRadialMenu
    │   └── panels/             # 12 specialized floating/drawer control panels
    └── settings/               # SettingsScreen (touch, stylus, performance, storage)
```

---

## 🚀 Building & Running

Ensure you have Android SDK 35 and JDK 17 installed on your development workstation or build machine:

```bash
cd /storage/emulated/0/test-folder/AuraPaint

# Build debug APK
./gradlew assembleDebug

# Install on connected Android device/emulator
./gradlew installDebug
```
