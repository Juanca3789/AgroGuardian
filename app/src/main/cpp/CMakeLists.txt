cmake_minimum_required(VERSION 3.22)
project(llama_android)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ===== FLAGS CRÍTICOS PARA LLAMA.CPP =====
# El -fno-finite-math-only es OBLIGATORIO (lo pide llama.cpp)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -ffast-math -fno-finite-math-only")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -fno-finite-math-only")

# ===== OPTIMIZACIONES ARM SOLO SI ES arm64-v8a =====
if(ANDROID_ABI STREQUAL "arm64-v8a")
    message(STATUS "✅ Enabling ARM optimizations for arm64-v8a")

    # Flags de arquitectura ARM (sin +dotprod si causa problemas)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -march=armv8-a+fp+simd")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -march=armv8-a+fp+simd")

    # Configurar llama.cpp para usar NEON
    set(GGML_NATIVE OFF CACHE BOOL "" FORCE)
    set(GGML_LTO OFF CACHE BOOL "" FORCE)
    set(GGML_CUDA OFF CACHE BOOL "" FORCE)
    set(GGML_METAL OFF CACHE BOOL "" FORCE)
    set(GGML_VULKAN OFF CACHE BOOL "" FORCE)

    add_definitions(-DGGML_USE_CPU)
    add_definitions(-DGGML_USE_NEON)
endif()

# Incluir llama.cpp
add_subdirectory(third_party/llama.cpp)

# Tu biblioteca JNI
add_library(llama_jni SHARED
        llama_jni.cpp
)

target_include_directories(llama_jni PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/third_party/llama.cpp/include
        ${CMAKE_CURRENT_SOURCE_DIR}/third_party/llama.cpp/src
)

target_link_libraries(llama_jni PRIVATE
        llama
        log
)