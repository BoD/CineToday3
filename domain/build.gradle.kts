plugins {
    id("library-conventions")
}

dependencies {
    // The domain module has no dependencies!
    // Well, except util
    implementation(projects.util)
}
