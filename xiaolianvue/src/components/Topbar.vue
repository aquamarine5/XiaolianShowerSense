<script setup lang="js">
import LineMdCompassTwotone from '~icons/line-md/compass-twotone?width=28px&height=28px';

const props = defineProps({
    washCount: {
        type: Number
    },
    washAvgTime: {
        type: String
    },
    useCount: {
        type: Number
    }
})

function jumpToGithub() {
    window.open("https://github.com/aquamarine5/XiaolianWebHelper", "_blanks")
}

function jumpToDouyin() {
    window.open("https://www.douyin.com/user/MS4wLjABAAAApuyqymIaQkpvKkbdH1X6W3A6XEgJl7kddGrZHxipJ7TbA1lCRaPJK5gZ1KX7pR1n", "_blanks")
}
</script>

<template>
    <div class="topbar_bg">
        <div class="topbar_container">
            <div class="topbar_title">
                XiaolianWebHelper
            </div>
            <svg class="topbar_douyin" width="24" height="24" viewBox="0 0 30 34" fill="none" @click="jumpToDouyin">
                <path
                    d="M11.6394 13.4334V12.1225C11.184 12.0679 10.7286 12.0315 10.255 12.0315C4.60798 12.0315 -0.000732422 16.6195 -0.000732422 22.2817C-0.000732422 25.741 1.72981 28.8178 4.37116 30.6749C2.65884 28.836 1.62052 26.3964 1.62052 23.7018C1.63873 18.1125 6.10171 13.5608 11.6394 13.4334Z"
                    fill="#00FAF0" />
                <path
                    d="M11.8766 28.3447C14.3905 28.3447 16.4489 26.342 16.54 23.8477V1.5448H20.6205C20.5294 1.08963 20.4929 0.616269 20.4929 0.124695H14.9188V22.4276C14.8277 24.9219 12.7692 26.9246 10.2554 26.9246C9.47209 26.9246 8.72522 26.7243 8.08765 26.3784C8.94381 27.58 10.3282 28.3447 11.8766 28.3447Z"
                    fill="#00FAF0" />
                <path
                    d="M28.2514 9.11866V7.88063C26.7031 7.88063 25.2458 7.42547 24.0253 6.62439C25.1182 7.86243 26.5938 8.75453 28.2514 9.11866Z"
                    fill="#00FAF0" />
                <path
                    d="M24.0449 6.62562C22.8609 5.26014 22.1323 3.49411 22.1323 1.54602H20.6385C21.0393 3.65797 22.2962 5.47862 24.0449 6.62562Z"
                    fill="#FE2C55" />
                <path
                    d="M10.2551 17.5846C7.66836 17.5846 5.57349 19.6783 5.57349 22.2637C5.57349 24.0661 6.5936 25.6136 8.08733 26.3965C7.54084 25.6319 7.19473 24.6851 7.19473 23.6656C7.19473 21.0802 9.28961 18.9865 11.8763 18.9865C12.3499 18.9865 12.8236 19.0593 13.2608 19.205V13.5246C12.8054 13.4699 12.3499 13.4335 11.8763 13.4335C11.8035 13.4335 11.7124 13.4335 11.6395 13.4335V17.8031C11.2023 17.6574 10.7469 17.5846 10.2551 17.5846Z"
                    fill="#FE2C55" />
                <path
                    d="M28.2523 9.11871V13.4519C25.356 13.4519 22.6964 12.5233 20.5104 10.9576V22.2638C20.5104 27.9078 15.9199 32.514 10.2547 32.514C8.06873 32.514 6.04672 31.8222 4.38904 30.6569C6.26531 32.6597 8.92489 33.9341 11.8941 33.9341C17.5412 33.9341 22.1499 29.3461 22.1499 23.6839V12.3777C24.3358 13.9434 27.0136 14.872 29.8918 14.872V9.30078C29.3271 9.28257 28.7806 9.22795 28.2523 9.11871Z"
                    fill="#FE2C55" />
                <path
                    d="M20.5094 22.264V10.9578C22.6953 12.5236 25.3731 13.4521 28.2513 13.4521V9.11898C26.5754 8.75485 25.1181 7.86275 24.0251 6.62471C22.2763 5.49591 21.0194 3.67526 20.6369 1.5451H16.5564V23.848C16.4653 26.3423 14.4069 28.345 11.8931 28.345C10.3265 28.345 8.94202 27.5803 8.10407 26.3969C6.61034 25.614 5.59023 24.0483 5.59023 22.264C5.59023 19.6787 7.6851 17.585 10.2718 17.585C10.7454 17.585 11.2191 17.6578 11.6562 17.8035V13.4339C6.1185 13.5614 1.65552 18.113 1.65552 23.6659C1.65552 26.3605 2.69384 28.8184 4.40617 30.639C6.06385 31.8042 8.10408 32.4961 10.2718 32.4961C15.9006 32.5143 20.5094 27.908 20.5094 22.264Z"
                    fill="white" />
            </svg>
            <svg class="topbar_github" @click="jumpToGithub">
                <path
                    d="M12.5.75C6.146.75 1 5.896 1 12.25c0 5.089 3.292 9.387 7.863 10.91.575.101.79-.244.79-.546 0-.273-.014-1.178-.014-2.142-2.889.532-3.636-.704-3.866-1.35-.13-.331-.69-1.352-1.18-1.625-.402-.216-.977-.748-.014-.762.906-.014 1.553.834 1.769 1.179 1.035 1.74 2.688 1.25 3.349.948.1-.747.402-1.25.733-1.538-2.559-.287-5.232-1.279-5.232-5.678 0-1.25.445-2.285 1.178-3.09-.115-.288-.517-1.467.115-3.048 0 0 .963-.302 3.163 1.179.92-.259 1.897-.388 2.875-.388.977 0 1.955.13 2.875.388 2.2-1.495 3.162-1.179 3.162-1.179.633 1.581.23 2.76.115 3.048.733.805 1.179 1.825 1.179 3.09 0 4.413-2.688 5.39-5.247 5.678.417.36.776 1.05.776 2.128 0 1.538-.014 2.774-.014 3.162 0 .302.216.662.79.547C20.709 21.637 24 17.324 24 12.25 24 5.896 18.854.75 12.5.75Z">
                </path>
            </svg>
        </div>
    </div>
    <div class="tipsbar_bg">
        <LineMdCompassTwotone class="tipsbar_icon"/>
        <div class="tipsbar_container">
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    当前统计洗浴次数：
                </div>
                <div class="tipsbar_value">
                    {{ props.washCount }}
                </div>
            </div>
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    平均洗浴时间：
                </div>
                <div class="tipsbar_value">
                    {{ props.washAvgTime }}
                </div>
            </div>
            <div class="tipsbar_column">
                <div class="tipsbar_text">
                    工具使用次数：
                </div>
                <div class="tipsbar_value">
                    {{ props.useCount }}
                </div>
            </div>
        </div>
    </div>
</template>

<style>
.tipsbar_column {
    display: flex;
}

.tipsbar_container {
    padding: 8px;
    padding-left: 20px;
    padding-bottom: 12px;
    font-size: small;
    font-weight: 600;
}

.tipsbar_icon {
    padding-left: 20px;
}

.tipsbar_bg {
    align-items: center;
    display: flex;
    background-color: #03a8f4a8;
    border-radius: 0px 0px 10px 10px;
}

.tipsbar_text {
    color: #b3e5fc;
}

.tipsbar_value {
    color: white;
    padding-left: 5px;
}

.topbar_bg {
    background-color: #03a9f4;
}

.topbar_container {
    padding-top: 15px;
    padding-inline: 20px;
    display: flex;
    height: 40px;
}

.topbar_title {
    font-weight: 800;
    font-size: larger;
    color: whitesmoke;
}

.topbar_github {
    cursor: pointer;
    justify-self: end;
    margin-left: 9px;
    width: 24px;
    height: 24px;
}
.topbar_douyin{
    cursor: pointer;
    justify-self: end;
    margin-left: auto;
}
</style>