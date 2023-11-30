java_binary(
    name = "Hairball",
    srcs = glob(["hairball/src/main/java/**/*.java","buckets/src/main/java/**/*.java","core/src/main/java/**/*.java"]),
    deps = ["//hairball_core:HairballCore",
        "//core:CatfoodCore",
        "@maven//:com_amazonaws_aws_java_sdk_bom",
        "@maven//:com_amazonaws_aws_java_sdk_s3",
        "@maven//:com_amazonaws_aws_java_sdk_core",
        "@maven//:io_vertx_vertx_core",
        "@maven//:com_helger_ph_pdf_layout4",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_projectlombok_lombok",
        "@maven//:org_apache_pdfbox_pdfbox",
        ":lombok"],
    main_class = "com.giantelectronicbrain.catfood.hairball.StandAloneHairball"
)

java_plugin(
    name = "lombok_plugin",
    processor_class = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
    generates_api = True,
    deps = ["@maven//:org_projectlombok_lombok"],
    visibility = ["//visibility:public"]
)
java_library(
    name = "lombok",
    exports = [
        "@maven//:org_projectlombok_lombok",
    ],
    exported_plugins = [
        ":lombok_plugin"
    ],
)
